package org.flightservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightResponseDTO{
    private Long id;
    private String flightNumber;
    private String sourceCode;
    private String sourceCity;
    private String destinationCode;
    private String destinationCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private List<FlightSeatResponseDTO> seatClasses;
}