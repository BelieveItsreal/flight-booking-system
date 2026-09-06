<h1 align="center">✈️ SkyRoute – Microservices Flight Booking Platform</h1>

<p align="center">
  <b>Java | Spring Boot | Spring Cloud gRPC | Kafka | PostgreSQL | Docker</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/SpringBoot-3.x-brightgreen?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Database-PostgreSQL-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Messaging-Kafka-black?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Architecture-Microservices-yellow?style=for-the-badge" />
  <img width="791" height="871" alt="system design for flight app" src="https://github.com/user-attachments/assets/59d02219-a190-4dee-84c1-fb34eb203329" />
</p>

<hr/>

<h2>🚀 Project Overview</h2>

<p>
A backend flight booking system split into independently deployable Spring Boot services, communicating over
<b>REST</b> (client-facing), <b>gRPC</b> (service-to-service seat inventory operations), and
<b>Kafka</b> (asynchronous booking-confirmation events). Each service owns its own PostgreSQL database —
there is no shared schema.
</p>

<hr/>

<h2>🧩 Services</h2>

<table>
  <tr><th>Service</th><th>Port</th><th>Owns</th><th>Responsibility</th></tr>
  <tr><td>user-service</td><td>8081</td><td>userdb</td><td>Registration, login, JWT issuance, user/admin roles</td></tr>
  <tr><td>flight-service</td><td>8083 (REST) / 9091 (gRPC)</td><td>flightdb</td><td>Flight CRUD, search/filter, seat inventory, seat holds</td></tr>
  <tr><td>booking-service</td><td>8082</td><td>bookingdb</td><td>Booking creation/cancellation, orchestrates seat hold via gRPC, publishes booking-confirmed events</td></tr>
  <tr><td>email-service</td><td>8084</td><td>—</td><td>Consumes booking-confirmed Kafka events, sends confirmation email via Gmail SMTP</td></tr>
  <tr><td>api-gateway</td><td>8080</td><td>—</td><td>Scaffolded entry point; routing to downstream services not yet configured</td></tr>
</table>

<p>Shared gRPC/protobuf contracts live in the <code>shared-proto</code> module, consumed by both <code>flight-service</code> and <code>booking-service</code>.</p>

<hr/>

<h2>✨ Core Features</h2>

<h3>🔐 Auth (user-service)</h3>
<ul>
  <li>Registration (<code>/user/add</code>) and login (<code>/api/auth/login</code>) issuing a JWT</li>
  <li>Role-based access: <code>USER</code> / <code>ADMIN</code> (JWT carries role, validated by each service independently with a shared secret)</li>
  <li>Admin-only endpoints (flight management, admin creation) enforced via Spring Security</li>
</ul>

<h3>🛫 Flight & Seat Management (flight-service)</h3>
<ul>
  <li>Add/Update/Delete flights (admin-only), search by source/destination, filter/sort by price and time, pagination</li>
  <li>Per-flight seat inventory by class (Economy / Business) with atomic decrement/increment on reserve/release</li>
  <li>
    Seat hold lifecycle exposed over gRPC to booking-service:
    <ul>
      <li><code>PENDING</code> → seat decremented, hold row created</li>
      <li><code>CONFIRMED</code> → booking succeeded</li>
      <li><code>RELEASED</code> → booking-service explicitly released it (failure/cancellation)</li>
      <li><code>EXPIRED</code> → a scheduled job (<code>SeatHoldExpiryScheduler</code>, every 60s) reclaims seats from holds left <code>PENDING</code> past a configurable TTL (default 10 min), so a crashed booking-service can't strand seats permanently</li>
    </ul>
  </li>
</ul>

<h3>📖 Booking Flow (booking-service)</h3>
<ul>
  <li>Create booking → reserve seat over gRPC → persist booking → confirm hold over gRPC, with compensating release/rollback on failure at each step</li>
  <li>Cancel booking → releases the underlying seat</li>
  <li>Publishes a <code>booking-confirmed</code> Kafka event on successful booking</li>
  <li>Users can only view/cancel their own bookings; admins can view all</li>
</ul>

<h3>📧 Email Notifications (email-service)</h3>
<ul>
  <li>Kafka consumer (<code>groupId=email-service</code>, topic <code>booking-confirmed</code>) triggers a booking confirmation email over Gmail SMTP</li>
  <li>Decoupled from the booking transaction — a slow/down mail provider never blocks a booking</li>
</ul>

<h3>⚠️ Exception Handling</h3>
<ul>
  <li>Per-service centralized exception handlers, custom exception types, consistent JSON error responses</li>
</ul>

<hr/>

<h2>🧱 Tech Stack</h2>

<ul>
  <li><b>Language:</b> Java 17</li>
  <li><b>Backend:</b> Spring Boot 3.x, Spring Security, Spring Data JPA, Spring Kafka, Spring gRPC (grpc-spring-boot-starter)</li>
  <li><b>Database:</b> PostgreSQL (one database per service)</li>
  <li><b>Messaging:</b> Apache Kafka</li>
  <li><b>Service-to-service:</b> gRPC + Protocol Buffers (shared-proto module)</li>
  <li><b>Auth:</b> JWT</li>
  <li><b>Build:</b> Maven (multi-module)</li>
  <li><b>Infra:</b> Docker Compose (Postgres, Kafka, and all services)</li>
</ul>

<hr/>

<h2>📂 Project Structure</h2>

<pre>
flight-booking-system/
├── user-service/        # registration, login, JWT, roles
├── flight-service/      # flights, seat inventory, seat holds (REST + gRPC server)
├── booking-service/     # bookings, gRPC client, Kafka producer
├── email-service/       # Kafka consumer, SMTP sender
├── api-gateway/         # scaffolded, routing not yet wired
├── shared-proto/        # .proto contracts shared by flight-service & booking-service
├── docker/postgres-init/# creates per-service databases/roles on first boot
└── docker-compose.yml
</pre>

<hr/>

<h2>🔗 Key API Endpoints</h2>

<h3>user-service</h3>
<table>
  <tr><th>Method</th><th>Endpoint</th><th>Auth</th><th>Description</th></tr>
  <tr><td>POST</td><td>/user/add</td><td>Public</td><td>Register a user</td></tr>
  <tr><td>POST</td><td>/api/auth/login</td><td>Public</td><td>Login, returns JWT</td></tr>
  <tr><td>POST</td><td>/api/admin/create-admin</td><td>ADMIN</td><td>Create another admin</td></tr>
  <tr><td>GET</td><td>/user/id/{id}</td><td>Authenticated</td><td>Get user by id</td></tr>
</table>

<h3>flight-service</h3>
<table>
  <tr><th>Method</th><th>Endpoint</th><th>Auth</th><th>Description</th></tr>
  <tr><td>POST</td><td>/flights/add</td><td>ADMIN</td><td>Add flight with seat classes/prices</td></tr>
  <tr><td>GET</td><td>/flights/allFlights</td><td>Authenticated</td><td>Paginated flight list</td></tr>
  <tr><td>GET</td><td>/flights/search?source=&destination=</td><td>Authenticated</td><td>Search flights</td></tr>
  <tr><td>GET</td><td>/flights/filter/price?minPrice=&maxPrice=</td><td>Authenticated</td><td>Filter by price range</td></tr>
  <tr><td>PUT</td><td>/flights/update/{id}</td><td>ADMIN</td><td>Update flight</td></tr>
  <tr><td>DELETE</td><td>/flights/delete/{id}</td><td>ADMIN</td><td>Delete flight</td></tr>
</table>

<h3>booking-service</h3>
<table>
  <tr><th>Method</th><th>Endpoint</th><th>Auth</th><th>Description</th></tr>
  <tr><td>POST</td><td>/bookings/create</td><td>Authenticated</td><td>Reserve a seat and create a booking</td></tr>
  <tr><td>GET</td><td>/bookings/all</td><td>Authenticated</td><td>Own bookings (all bookings if ADMIN)</td></tr>
  <tr><td>GET</td><td>/bookings/get/{id}</td><td>Authenticated</td><td>Get a booking by id (owner or ADMIN)</td></tr>
  <tr><td>PUT</td><td>/bookings/cancel/{id}</td><td>Authenticated</td><td>Cancel a booking and release its seat</td></tr>
</table>

<hr/>

<h2>⚙️ Setup & Run</h2>

<h3>1. Clone & configure secrets</h3>
<pre>
git clone https://github.com/&lt;your-org&gt;/flight-booking-system.git
cd flight-booking-system
</pre>
Create a <code>.env</code> file in the repo root with:
<pre>
JWT_SECRET=&lt;a long random string, shared by all services&gt;
MAIL_USERNAME=&lt;gmail address used for sending confirmations&gt;
MAIL_APP_PASSWORD=&lt;gmail app password&gt;
EMAIL_SERVICE_API_KEY=&lt;api key for email-service's own protected endpoint&gt;
</pre>

<h3>2. Run everything with Docker Compose</h3>
<pre>
docker-compose up --build -d
docker-compose ps
docker-compose logs -f
</pre>
This starts Postgres (with per-service databases/roles auto-created), Kafka, and all four Spring Boot services.

<h3>3. Run a service locally instead (optional)</h3>
Start only the infra in Docker:
<pre>
docker-compose up -d postgres kafka
</pre>
Then run any service from your IDE/Maven with <code>JWT_SECRET</code> (and, for email-service, the mail vars) set as environment variables — each service's <code>application.properties</code> already points at <code>localhost:5432</code> and <code>localhost:9094</code>.

<blockquote>
<b>Note:</b> if you also have a native PostgreSQL install on Windows, it will bind <code>127.0.0.1:5432</code> ahead of Docker's container and silently intercept connections meant for Docker. Stop the local <code>postgresql-x64-*</code> service before running services locally against the Docker database.
</blockquote>

<h3>4. Bootstrapping the first admin</h3>
There's currently no seed admin account. Register a normal user via <code>/user/add</code>, then promote it directly in Postgres:
<pre>
docker exec flight-booking-postgres psql -U user_service_user -d userdb \
  -c "UPDATE users SET role='ADMIN' WHERE email='you@example.com';"
</pre>
Log in again afterward to get a JWT carrying the ADMIN role.

<hr/>

<h2>🎯 Roadmap</h2>
<ul>
  <li>Wire up api-gateway routing so clients don't need to know individual service ports</li>
  <li>Seed a default admin account instead of the manual SQL bootstrap step</li>
  <li>Payment integration</li>
  <li>React frontend</li>
</ul>

<hr/>

<h2>👨‍💻 Author</h2>
<p><b>Atanu Das</b><br/>Java Backend Developer</p>

<hr/>

<h2>⭐ Support</h2>
<p>If you like this project, give it a ⭐ on GitHub!</p>
