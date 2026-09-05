package org.flightservice.service;
import org.flightservice.dto.FlightResponseDTO;
import org.flightservice.entity.SeatHold;
import org.flightservice.enums.SeatClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface FlightSeatService {
    Page<FlightResponseDTO> getFlightByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);
    SeatHold reserveSeat(Long flightId, SeatClass seatClass);
    void confirmSeat(Long holdId);
    void releaseHold(Long holdId);
    void releaseSeat(Long flightSeatId);
    void expireStaleHolds();

}
