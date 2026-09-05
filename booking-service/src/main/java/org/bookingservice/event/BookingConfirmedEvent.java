package org.bookingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmedEvent {
    private String eventId;
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private Long flightId;
    private String flightNumber;
    private String seatClass;
    private LocalDateTime bookingTime;
    private LocalDateTime departureTime;
    private Double priceAtBooking;
}
