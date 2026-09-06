package org.flightservice.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;
import org.flightservice.dto.FlightResponseDTO;
import org.flightservice.entity.Flight;
import org.flightservice.entity.FlightSeat;
import org.flightservice.entity.SeatHold;
import org.flightservice.enums.HoldStatus;
import org.flightservice.enums.SeatClass;
import org.flightservice.exception.FlightNotFoundException;
import org.flightservice.exception.SeatClassNotFoundException;
import org.flightservice.exception.SeatUnavailableException;
import org.flightservice.mapper.FlightMapper;
import org.flightservice.repository.FlightRepository;
import org.flightservice.repository.FlightSeatRepository;
import org.flightservice.repository.SeatHoldRepository;
import org.flightservice.service.FlightSeatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class FlightSeatServiceImpl implements FlightSeatService{

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final FlightSeatRepository flightSeatRepository;
    private final SeatHoldRepository seatHoldRepository;
    @Value("${seat.hold.ttl-minutes:10}")
    private int holdTtMinutes;

    public FlightSeatServiceImpl(FlightRepository flightRepository, FlightMapper flightMapper, FlightSeatRepository flightSeatRepository,
                                 SeatHoldRepository seatHoldRepository){
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
        this.flightSeatRepository = flightSeatRepository;
        this.seatHoldRepository = seatHoldRepository;
    }

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

    @Override
    @Transactional
    public SeatHold reserveSeat(Long flightId, SeatClass seatClass) {
        int updatedRows = flightSeatRepository.decrementIfAvailable(flightId, seatClass);
        if (updatedRows == 0){
            boolean exists = flightSeatRepository.findByFlightIdAndSeatClass(flightId, seatClass).isPresent();
            if (!exists){
                throw new SeatClassNotFoundException(
                        "No " + seatClass + " seats configured for flight " + flightId);
            }
            throw new SeatUnavailableException(
                    "No " + seatClass + " seats available for flight " + flightId);
        }
        FlightSeat seat = flightSeatRepository.findByFlightIdAndSeatClass(flightId, seatClass)
                .orElseThrow(() -> new SeatClassNotFoundException(
                        "No " + seatClass + " seats configured for flight " + flightId));
        SeatHold hold = new SeatHold();
        hold.setFlightSeatId(seat.getId());
        hold.setPrice(seat.getPrice());
        hold.setStatus(HoldStatus.PENDING);
        hold.setReservedAt(LocalDateTime.now());
        return seatHoldRepository.save(hold);
    }

    @Override
    @Transactional
    public void confirmSeat(Long holdId) {
        int updated = seatHoldRepository.updateStatusIfCurrent(holdId, HoldStatus.PENDING, HoldStatus.CONFIRMED);
        if (updated == 0){
            throw new SeatClassNotFoundException("No pending seat hold found with id " + holdId);
        }
    }

    @Override
    @Transactional
    public void releaseHold(Long holdId) {
        SeatHold hold = seatHoldRepository.findById(holdId)
                .orElseThrow(() -> new SeatClassNotFoundException("No seat hold found with id " + holdId));
        int updated = seatHoldRepository.updateStatusIfCurrent(holdId, HoldStatus.PENDING, HoldStatus.RELEASED);
        if (updated > 0){
            flightSeatRepository.increment(hold.getFlightSeatId());
        }
    }

    @Override
    @Transactional
    public void releaseSeat(Long flightSeatId) {
        int updatedRows = flightSeatRepository.increment(flightSeatId);
        if (updatedRows == 0){
             throw new SeatClassNotFoundException("No flight seat found with id " + flightSeatId);
        }
    }

    @Override
    public void expireStaleHolds() {
        LocalDateTime cuttOff = LocalDateTime.now().minusMinutes(holdTtMinutes);
        List<SeatHold> staleHolds = seatHoldRepository.findByStatusAndReservedAtBefore(HoldStatus.PENDING, cuttOff);
        for (SeatHold hold : staleHolds){
            int updated = seatHoldRepository.updateStatusIfCurrent(hold.getId(), HoldStatus.PENDING, HoldStatus.EXPIRED);
            if (updated > 0){
                flightSeatRepository.increment(hold.getFlightSeatId());
            }
        }
    }
}