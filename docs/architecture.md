# SkyRoute — Architecture

A Maven multi-module microservices platform: Java 21, Spring Boot 3.5, PostgreSQL (database-per-service), Apache Kafka (KRaft), and gRPC for service-to-service seat inventory operations.

## Services at a glance

| Service | Port(s) | Database | Responsibility |
|---|---|---|---|
| api-gateway | 8080 | — | Spring Cloud Gateway scaffold — **no routes configured yet**; clients call services directly |
| user-service | 8081 | `userdb` | Registration, login, JWT issuance (HS256), USER/ADMIN roles |
| booking-service | 8082 | `bookingdb` | Booking create/cancel, seat-hold orchestration over gRPC, transactional outbox → Kafka |
| flight-service | 8083 (REST), 9091 (gRPC) | `flightdb` | Flight CRUD/search, seat inventory, seat-hold lifecycle, hold-expiry scheduler |
| email-service | 8084 | — | Consumes `booking-confirmed` from Kafka, sends confirmation email via Gmail SMTP |
| shared-proto | — | — | Protobuf/gRPC contracts (`flight_seat.proto`), compile-time dependency of flight-service and booking-service |

## System overview

```mermaid
flowchart TB
    Client([Client])
    Gateway["api-gateway :8080<br/>(scaffold — no routes yet)"]

    Client -.->|"intended entry point"| Gateway

    subgraph Services["Spring Boot services"]
        User["user-service :8081<br/>auth, JWT issuance"]
        Flight["flight-service :8083<br/>flights, seat inventory<br/>gRPC server :9091"]
        Booking["booking-service :8082<br/>bookings, outbox"]
        Email["email-service :8084<br/>confirmation mails"]
    end

    Client -->|"REST (login → JWT)"| User
    Client -->|"REST + JWT"| Flight
    Client -->|"REST + JWT"| Booking

    Booking -->|"gRPC :9091 plaintext<br/>GetFlight / ReserveSeat / ConfirmSeat<br/>ReleaseHold / ReleaseSeat"| Flight

    subgraph Postgres["PostgreSQL :5432 (one DB per service)"]
        UserDB[("userdb")]
        FlightDB[("flightdb<br/>flights, flight_seats, seat_holds")]
        BookingDB[("bookingdb<br/>bookings, outbox_events")]
    end

    User --> UserDB
    Flight --> FlightDB
    Booking --> BookingDB

    Kafka[["Kafka (KRaft) — topic: booking-confirmed<br/>host :9094 / in-network kafka:9092"]]

    Booking -->|"OutboxRelay polls outbox_events<br/>every 5s, publishes batch ≤50"| Kafka
    Kafka -->|"consumer group: email-service"| Email

    Gmail["Gmail SMTP<br/>smtp.gmail.com:587"]
    Email -->|"STARTTLS"| Gmail
    Gmail -->|"booking confirmation"| Inbox([User inbox])

    Proto["shared-proto module<br/>flight_seat.proto"]
    Proto -.->|"build-time dependency"| Flight
    Proto -.->|"build-time dependency"| Booking
```

Key points:

- **No service discovery / config server** — addresses are static (`grpc.client.flight-service.address=static://localhost:9091`, Kafka bootstrap hard-coded per profile).
- **JWT with a shared secret**: user-service issues HS256 tokens (`sub`=email, claims `uid`, `role`); flight-service and booking-service each verify independently with the same `JWT_SECRET`. There is no shared auth library — the JWT filter/service classes are duplicated per service.
- **gRPC :9091 is plaintext and unauthenticated** — booking-service's identity claims are trusted implicitly inside the network.
- **email-service** has no Spring Security; its REST endpoint (`POST /api/emails/booking-confirmation`) is protected by a static `X-Internal-Api-Key` header.

## Booking creation flow (with compensation paths)

`BookingServiceImpl.createBooking` is `@Transactional`; the booking row and its outbox event commit atomically. Kafka publication is decoupled via the **transactional outbox** pattern.

```mermaid
sequenceDiagram
    autonumber
    actor C as Client
    participant B as booking-service
    participant F as flight-service (gRPC :9091)
    participant DB as bookingdb
    participant R as OutboxRelay (every 5s)
    participant K as Kafka
    participant E as email-service
    participant G as Gmail SMTP

    C->>B: POST /bookings/create (JWT)
    B->>F: GetFlight(flightId)
    F-->>B: flight number, departure time
    B->>F: ReserveSeat(flightId, seatClass)
    Note over F: atomic seat decrement<br/>+ SeatHold(PENDING)
    F-->>B: holdId, flightSeatId, price
    alt seat unavailable
        F-->>B: success=false
        B-->>C: SeatUnavailableException
    end
    B->>DB: save Booking (CONFIRMED)
    alt save fails
        B->>F: ReleaseSeat(flightSeatId) — compensate
        B-->>C: error
    end
    B->>F: ConfirmSeat(holdId)  [PENDING → CONFIRMED]
    alt confirm fails
        B->>DB: delete Booking
        B->>F: ReleaseHold(holdId) — best effort
        B-->>C: error
    end
    B->>DB: insert outbox_events row (PENDING)<br/>same transaction as Booking
    B-->>C: 201 Created

    R->>DB: poll PENDING events (batch ≤ 50)
    R->>K: send("booking-confirmed", key=bookingId, JSON)
    R->>DB: mark SENT + publishedAt
    K->>E: BookingConfirmedEvent (group: email-service)
    E->>G: send confirmation email (STARTTLS :587)
```

Delivery is **at-least-once**: if the relay crashes between `send` and `markSent`, the event is re-published on the next poll — consumers must tolerate duplicates.

## Seat hold lifecycle

Holds live in flight-service's `seat_holds` table. A scheduler (`SeatHoldExpiryScheduler`, every 60s) reclaims seats from holds left `PENDING` past the TTL (`seat.hold.ttl-minutes`, default 10), so a crashed booking-service cannot strand inventory.

```mermaid
stateDiagram-v2
    [*] --> PENDING: ReserveSeat — seat decremented, hold created
    PENDING --> CONFIRMED: ConfirmSeat (booking committed)
    PENDING --> RELEASED: ReleaseHold (booking-service compensation)
    PENDING --> EXPIRED: expiry scheduler — TTL exceeded, seat restored
    CONFIRMED --> [*]
    RELEASED --> [*]
    EXPIRED --> [*]
```

## Deployment (docker-compose)

```mermaid
flowchart LR
    subgraph Host["Host machine"]
        subgraph Net["docker network: backend"]
            PG["flight-booking-postgres<br/>postgres:18<br/>:5432"]
            KF["flight-booking-kafka<br/>apache/kafka:3.8.0 (KRaft, no Zookeeper)<br/>internal kafka:9092 / host :9094"]
            GW["api-gateway :8080"]
            US["user-service :8081"]
            BS["booking-service :8082"]
            FS["flight-service :8083<br/>gRPC :9091 (not published to host)"]
            ES["email-service :8084"]
        end
        Vol[("volume: postgres_data")]
    end

    PG --- Vol
    US -->|healthcheck-gated| PG
    FS -->|healthcheck-gated| PG
    BS --> PG
    BS --> KF
    KF --> ES
    BS --> FS
```

- `docker/postgres-init/init-databases.sql` creates `userdb`, `flightdb`, `bookingdb` and their per-service roles on first boot.
- Kafka exposes two listeners: `kafka:9092` for containers on the `backend` network, `localhost:9094` for services run on the host (IDE/Maven). Services' `application.properties` default to the host listener.
- Secrets come from `.env`: `JWT_SECRET`, `MAIL_USERNAME`, `MAIL_APP_PASSWORD`, `EMAIL_SERVICE_API_KEY`.

## Messaging

| Topic | Producer | Consumer (group) | Payload | Semantics |
|---|---|---|---|---|
| `booking-confirmed` | booking-service `OutboxRelay` (key = bookingId) | email-service (`email-service`) | `BookingConfirmedEvent` JSON | at-least-once via transactional outbox |
