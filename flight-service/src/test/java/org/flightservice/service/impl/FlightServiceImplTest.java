package org.flightservice.service.impl;


import org.flightservice.dto.FlightRequestDTO;
import org.flightservice.dto.FlightResponseDTO;
import org.flightservice.entity.Flight;
import org.flightservice.exception.FlightNotFoundException;
import org.flightservice.mapper.FlightMapper;
import org.flightservice.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceImplTest {
    @Mock
    private FlightRepository flightRepository;
    
    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightServiceImpl flightService;

    @Test
    void addFlight_saveEntityAndReturnDTO(){
        FlightRequestDTO request = new FlightRequestDTO();
        Flight flight = new Flight();
        FlightResponseDTO responseDTO = new FlightResponseDTO();

        when(flightMapper.toEntity(request)).thenReturn(flight);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDTO(flight)).thenReturn(responseDTO);

        FlightResponseDTO result = flightService.addFlight(request);

        assertThat(result).isSameAs(responseDTO);
        verify(flightRepository).save(flight);
    }

    @Test
    void getAllFlights_returnsPageOfDTOs(){
        Pageable pageable = PageRequest.of(0, 10);
        Flight flight = new Flight();
        FlightResponseDTO dto = new FlightResponseDTO();
        Page<Flight> page = new PageImpl<>(Collections.singletonList(flight));

        when(flightRepository.findAll(pageable)).thenReturn(page);
        when(flightMapper.toDTO(flight)).thenReturn(dto);

        Page<FlightResponseDTO> result = flightService.getAllFlights(pageable);
        assertThat(result.getContent()).containsExactly(dto);
    }

    @Test
    void getFlightById_found_returnDTO(){
        Flight flight = new Flight();
        FlightResponseDTO dto = new FlightResponseDTO();

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightMapper.toDTO(flight)).thenReturn(dto);

        assertThat(flightService.getFlightById(1L)).isSameAs(dto);
    }

    @Test
    void getFlightById_throwsFlightNotFoundException(){
        when(flightRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->  flightService.getFlightById(99L))
            .isInstanceOf(FlightNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void updateFlight_found_updatesAndReturnsDTO() {
        FlightRequestDTO request = new FlightRequestDTO();
        Flight existing = new Flight();
        FlightResponseDTO dto = new FlightResponseDTO();

        when(flightRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(flightRepository.save(existing)).thenReturn(existing);
        when(flightMapper.toDTO(existing)).thenReturn(dto);

        assertThat(flightService.updateFlight(1L, request)).isSameAs(dto);
        verify(flightMapper).updateEntity(request, existing);
        verify(flightRepository).save(existing);
    }

    @Test
    void updateFlight_notFound_throwsFlightNotFoundException() {
        when(flightRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.updateFlight(99L, new FlightRequestDTO()))
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- deleteFlight ----

    @Test
    void deleteFlight_found_deletesEntity() {
        Flight flight = new Flight();

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        flightService.deleteFlight(1L);

        verify(flightRepository).delete(flight);
    }

    @Test
    void deleteFlight_notFound_throwsFlightNotFoundException() {
        when(flightRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.deleteFlight(99L))
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- searchFlights — if/else branch coverage ----

    @Test
    void searchFlights_withSourceAndDestination_callsFindByBoth() {
        Pageable pageable = PageRequest.of(0, 10);
        Flight flight = new Flight();
        FlightResponseDTO dto = new FlightResponseDTO();
        Page<Flight> page = new PageImpl<>(Collections.singletonList(flight));

        when(flightRepository.findBySourceCodeAndDestinationCode("JFK", "LAX", pageable)).thenReturn(page);
        when(flightMapper.toDTO(flight)).thenReturn(dto);

        Page<FlightResponseDTO> result = flightService.searchFlights("JFK", "LAX", pageable);

        assertThat(result.getContent()).containsExactly(dto);
        verify(flightRepository).findBySourceCodeAndDestinationCode("JFK", "LAX", pageable);
        verify(flightRepository, never()).findBySourceCode(any(), any());
        verify(flightRepository, never()).findByDestinationCode(any(), any());
        verify(flightRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void searchFlights_withSourceOnly_callsFindBySource() {
        Pageable pageable = PageRequest.of(0, 10);
        Flight flight = new Flight();
        FlightResponseDTO dto = new FlightResponseDTO();
        Page<Flight> page = new PageImpl<>(Collections.singletonList(flight));

        when(flightRepository.findBySourceCode("JFK", pageable)).thenReturn(page);
        when(flightMapper.toDTO(flight)).thenReturn(dto);

        Page<FlightResponseDTO> result = flightService.searchFlights("JFK", null, pageable);

        assertThat(result.getContent()).containsExactly(dto);
        verify(flightRepository).findBySourceCode("JFK", pageable);
        verify(flightRepository, never()).findBySourceCodeAndDestinationCode(any(), any(), any());
        verify(flightRepository, never()).findByDestinationCode(any(), any());
        verify(flightRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void searchFlights_withDestinationOnly_callsFindByDestination() {
        Pageable pageable = PageRequest.of(0, 10);
        Flight flight = new Flight();
        FlightResponseDTO dto = new FlightResponseDTO();
        Page<Flight> page = new PageImpl<>(Collections.singletonList(flight));

        when(flightRepository.findByDestinationCode("LAX", pageable)).thenReturn(page);
        when(flightMapper.toDTO(flight)).thenReturn(dto);

        Page<FlightResponseDTO> result = flightService.searchFlights(null, "LAX", pageable);

        assertThat(result.getContent()).containsExactly(dto);
        verify(flightRepository).findByDestinationCode("LAX", pageable);
        verify(flightRepository, never()).findBySourceCodeAndDestinationCode(any(), any(), any());
        verify(flightRepository, never()).findBySourceCode(any(), any());
        verify(flightRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void searchFlights_withNoCriteria_callsFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Flight flight = new Flight();
        FlightResponseDTO dto = new FlightResponseDTO();
        Page<Flight> page = new PageImpl<>(Collections.singletonList(flight));

        when(flightRepository.findAll(pageable)).thenReturn(page);
        when(flightMapper.toDTO(flight)).thenReturn(dto);

        Page<FlightResponseDTO> result = flightService.searchFlights(null, null, pageable);

        assertThat(result.getContent()).containsExactly(dto);
        verify(flightRepository).findAll(pageable);
        verify(flightRepository, never()).findBySourceCodeAndDestinationCode(any(), any(), any());
        verify(flightRepository, never()).findBySourceCode(any(), any());
        verify(flightRepository, never()).findByDestinationCode(any(), any());
    }

    @Test
    void searchFlights_emptyResult_throwsFlightNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(flightRepository.findBySourceCodeAndDestinationCode("JFK", "LAX", pageable))
                .thenReturn(Page.empty());

        assertThatThrownBy(() -> flightService.searchFlights("JFK", "LAX", pageable))
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessageContaining("No flights found");
    }
}
