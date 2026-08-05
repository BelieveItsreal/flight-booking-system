package org.bookingservice.dto;

import org.bookingservice.enums.SeatClass;

import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    @NotNull(message = "Id cannot be null")
    private Long flightId;

    @NotNull(message = "Seatclass cannot be null")
    private SeatClass seatClass;

    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "^[A-Z0-9]{6,9}$", message = "Invalid passport number format")
    private String passportNumber;
}
