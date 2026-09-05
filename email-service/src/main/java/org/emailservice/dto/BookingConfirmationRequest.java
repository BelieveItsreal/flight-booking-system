package org.emailservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmationRequest {
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String toEmail;
    @NotNull(message = "Booking id is required")
    private Long bookingId;
    @NotBlank(message = "Flight Number is required")
    private String flightNumber;
    private String seatClass;
    private LocalDateTime bookingTime;
    private LocalDateTime departureTime;
    private Double priceAtBooking;
}
