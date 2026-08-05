package org.flightservice.service;

import org.flightservice.dto.FlightRequestDTO;
import org.flightservice.dto.FlightResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FlightService {

    FlightResponseDTO addFlight(FlightRequestDTO flight);

    Page<FlightResponseDTO> getAllFlights(Pageable pageable);

    FlightResponseDTO getFlightById(Long id);

    FlightResponseDTO updateFlight(Long id, FlightRequestDTO updatedFlightData);

    void deleteFlight(Long id);

    Page<FlightResponseDTO> searchFlights(String source, String destination, Pageable pageable);

    
}
