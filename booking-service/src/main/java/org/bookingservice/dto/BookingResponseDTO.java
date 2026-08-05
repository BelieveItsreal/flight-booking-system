package org.bookingservice.dto;

import java.time.LocalDateTime;

import org.bookingservice.enums.BookingStatus;
import org.bookingservice.enums.SeatClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private Long bookingId;
    private Long flightId;
    private Long flightSeatId;
    private Long userId;
    private SeatClass seatClass;
    private String passportNumber;
    private Double priceAtBooking;
    private LocalDateTime bookingTime;
    private BookingStatus status;
}
