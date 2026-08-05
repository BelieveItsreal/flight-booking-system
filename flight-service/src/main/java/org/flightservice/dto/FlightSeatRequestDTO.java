package org.flightservice.dto;

import org.flightservice.enums.SeatClass;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class FlightSeatRequestDTO {
    
    @NotNull(message = "SeatClass are required")
    private SeatClass seatClass;

    @NotNull(message = "Available seats is required")
    @Min(value = 1, message = "Available seats must be at least 1")
    private int availableSeats;

    @NotNull(message = "price can not be null")
    @Min(value = 0, message = "Price cannot be negative")
    private double price;
}
