package org.flightservice.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.flightservice.dto.FlightRequestDTO;
import org.flightservice.dto.FlightResponseDTO;
import org.flightservice.dto.FlightSeatResponseDTO;
import org.flightservice.entity.Flight;
import org.flightservice.entity.FlightSeat;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper{
    public FlightResponseDTO toDTO(Flight flight) {
        FlightResponseDTO dto= new FlightResponseDTO();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setSourceCode(flight.getSourceCode());
        dto.setSourceCity(flight.getSourceCity());
        dto.setDestinationCode(flight.getDestinationCode());
        dto.setDestinationCity(flight.getDestinationCity());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setSeatClasses(flight.getSeatClasses().stream()
        .map(seat -> {
            FlightSeatResponseDTO seatDto = new FlightSeatResponseDTO();
            seatDto.setId(seat.getId());
            seatDto.setSeatClass(seat.getSeatClass());
            seatDto.setAvailableSeats(seat.getAvailableSeats());
            seatDto.setPrice(seat.getPrice());
            return seatDto;
        }).toList());
        return dto;
    }

    public Flight toEntity(FlightRequestDTO request){
        Flight flight = new Flight();
        mapRequestToFlight(request, flight);
        return flight;
    }

    public void updateEntity(FlightRequestDTO request, Flight flight){
        mapRequestToFlight(request, flight);
    }

    private void mapRequestToFlight(FlightRequestDTO request, Flight flight) {
        flight.setFlightNumber(request.getFlightNumber());
        flight.setSourceCode(request.getSourceCode());
        flight.setSourceCity(request.getSourceCity());
        flight.setDestinationCode(request.getDestinationCode());
        flight.setDestinationCity(request.getDestinationCity());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        List<FlightSeat> seats = request.getSeatClasses().stream().map(s -> {
            FlightSeat seat = new FlightSeat();
            seat.setSeatClass(s.getSeatClass());
            seat.setAvailableSeats(s.getAvailableSeats());
            seat.setPrice(s.getPrice());
            seat.setFlight(flight);
            return seat;
        }).collect(Collectors.toList());

        if (flight.getSeatClasses() == null) {
            flight.setSeatClasses(seats);
        }else{
            flight.getSeatClasses().clear();
            flight.getSeatClasses().addAll(seats);
        }
    }
}
