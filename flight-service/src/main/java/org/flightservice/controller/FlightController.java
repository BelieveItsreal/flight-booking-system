package org.flightservice.controller;

import org.flightservice.dto.FlightRequestDTO;
import org.flightservice.dto.FlightResponseDTO;
import org.flightservice.service.FlightSeatService;
import org.flightservice.service.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/flights")
public class FlightController {
    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightSeatService flightSeatService;

    @PostMapping("/add")
    public ResponseEntity<FlightResponseDTO> addFlight(@RequestBody FlightRequestDTO flight){
        FlightResponseDTO savedFlight = flightService.addFlight(flight);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
    }

    @GetMapping("/allFlights")
    public ResponseEntity<Page<FlightResponseDTO>> getAllFlights(
        @PageableDefault(size = 5, sort = "id")
        Pageable pageable
        )
    {
        return ResponseEntity.status(HttpStatus.OK).body(flightService.getAllFlights(pageable));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<FlightResponseDTO> getFlightById(@PathVariable Long id){
        FlightResponseDTO getById = flightService.getFlightById(id);
        return ResponseEntity.status(HttpStatus.OK).body(getById);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<FlightResponseDTO> updateFlightDetails(@RequestBody FlightRequestDTO updatedData, @PathVariable Long id){
        FlightResponseDTO updatedFlight = flightService.updateFlight(id, updatedData);
        return ResponseEntity.status(HttpStatus.OK).body(updatedFlight);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id){
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FlightResponseDTO>>searchFlights(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @PageableDefault(size = 5)
            Pageable pageable){
        Page<FlightResponseDTO> output = flightService.searchFlights(source, destination, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(output);
    }

    @GetMapping("/filter/price")
    public ResponseEntity<Page<FlightResponseDTO>>filterByPrice(
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @PageableDefault(size = 5, sort = "departureTime")
        Pageable pageable){
        Page<FlightResponseDTO> output = flightSeatService.getFlightByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(output);
    }
}
