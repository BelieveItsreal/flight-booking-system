package org.flightservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FlightRequestDTO {

    @NotBlank(message = "flight number is required")
    private String flightNumber;

    @NotBlank(message = "Source code is required")
    @Size(min = 3, max = 3, message = "Source code must be exactly 3 characters")
    private String sourceCode;

    @NotNull(message = "source city can not be null")
    private String sourceCity;

    @NotBlank(message = "Destination code is required")
    @Size(min = 3, max = 3, message = "Destination code must be exactly 3 characters")
    private String destinationCode;

    @NotNull(message = "destination city can not be null")
    private String destinationCity;

    @NotNull(message = "Departure time is required")
    @FutureOrPresent(message = "Departure time must be in the present or future")
    private LocalDateTime departureTime;

    @NotNull(message = "ArrivalTime time is required")
    @Future(message = "Arrival time must be in the future")
    private LocalDateTime arrivalTime; 

    @NotNull(message = "Seat classes are required")
    @Valid
    private List<FlightSeatRequestDTO> seatClasses;
}
