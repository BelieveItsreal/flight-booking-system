package org.flightservice.service.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.flightservice.dto.FlightResponseDTO;
import org.flightservice.entity.Flight;
import org.flightservice.exception.FlightNotFoundException;
import org.flightservice.mapper.FlightMapper;
import org.flightservice.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class FlightSeatServiceImplTest {
    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightSeatServiceImpl flightSeatService;

    private Flight flight;
    private FlightResponseDTO flightResponseDTO;

    @BeforeEach
    void setUp() {
        flight = new Flight();
        flight.setId(1L);
        flight.setFlightNumber("A101");
        flight.setSourceCode("DEL");
        flight.setSourceCity("Delhi");
        flight.setDestinationCode("BOM");
        flight.setDestinationCity("Mumbai");
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));

        flightResponseDTO = new FlightResponseDTO();
        flightResponseDTO.setId(1L);
        flightResponseDTO.setFlightNumber("AI101");
        flightResponseDTO.setSourceCode("DEL");
        flightResponseDTO.setSourceCity("Delhi");
        flightResponseDTO.setDestinationCode("BOM");
        flightResponseDTO.setDestinationCity("Mumbai");
    }

    @Test
    void getFlightByPriceRange_validSortAndFlightFound_returnMappedPage(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("departureTime").ascending());
        Page<Flight> flightPage = new PageImpl<>(List.of(flight), pageable, 1);

        when(flightRepository.findFlightsBySeatPriceRange(100.0, 500.0, pageable)).thenReturn(flightPage);
        when(flightMapper.toDTO(flight)).thenReturn(flightResponseDTO);

        Page<FlightResponseDTO> result = flightSeatService.getFlightByPriceRange(100.0,500.0,  pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFlightNumber()).isEqualTo("AI101");
    }

    @Test
    void getFlightByPriceRange_invalidSortField_throwsIllegalArgumentException(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("flightNumber").ascending());

        assertThatThrownBy(()-> flightSeatService.getFlightByPriceRange(100.0, 500.0, pageable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid sort field: flightNumber");

    }

    @Test
    void getFlightByPriceRange_noFlightFound_throwsFlightNotFoundException(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("departureTime").descending());
        Page<Flight> emptyPage = Page.empty(pageable);

        when(flightRepository.findFlightsBySeatPriceRange(100.0, 500.0, pageable)).thenReturn(emptyPage);

        assertThatThrownBy(()-> flightSeatService.getFlightByPriceRange(100.0, 500.0, pageable))
            .isInstanceOf(FlightNotFoundException.class)
            .hasMessage("Flight not found with given price range");
    }
}
