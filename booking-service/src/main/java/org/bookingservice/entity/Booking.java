package org.bookingservice.entity;

import java.time.LocalDateTime;

import org.bookingservice.enums.BookingStatus;
import org.bookingservice.enums.SeatClass;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // flight-service and user-service own these DBs now - only the foreign ids
    // are stored here. Hydrating full flight/user details happens over gRPC in a later phase.
    private Long flightId;

    private Long flightSeatId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

    private Double priceAtBooking;

    private String passportNumber;

    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public void setSe() {

    }
}
