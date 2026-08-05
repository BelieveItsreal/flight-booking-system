package org.flightservice.dto;

import org.flightservice.enums.SeatClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightSeatResponseDTO {
    private Long id;
    private SeatClass seatClass;
    private int availableSeats;
    private Double price;
}
