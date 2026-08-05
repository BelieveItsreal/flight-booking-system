package org.flightservice.service.impl;

import java.util.List;

import org.flightservice.dto.FlightResponseDTO;
import org.flightservice.entity.Flight;
import org.flightservice.exception.FlightNotFoundException;
import org.flightservice.mapper.FlightMapper;
import org.flightservice.repository.FlightRepository;
import org.flightservice.service.FlightSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class FlightSeatServiceImpl implements FlightSeatService{
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightMapper flightMapper;

    @Override
    public Page<FlightResponseDTO> getFlightByPriceRange(Double minPrice, Double maxPrice, Pageable pageable){
        List<String> allowedSortFields = List.of("departureTime", "arrivalTime");
        for(Sort.Order order : pageable.getSort()){
            if (!allowedSortFields.contains(order.getProperty())) {
                throw new IllegalArgumentException("Invalid sort field: "+ order.getProperty());
            }
        }
        Page<Flight> result = flightRepository.findFlightsBySeatPriceRange(minPrice, maxPrice, pageable);
        if (result.isEmpty()){
            throw new FlightNotFoundException("Flight not found with given price range");
        }
        return result.map(flightMapper::toDTO);
    }
}