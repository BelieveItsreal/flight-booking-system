package org.flightservice.service;
import org.flightservice.dto.FlightResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface FlightSeatService {
    Page<FlightResponseDTO> getFlightByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);
}
