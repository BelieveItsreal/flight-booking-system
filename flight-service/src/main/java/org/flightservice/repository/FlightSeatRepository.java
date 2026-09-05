package org.flightservice.repository;

import java.util.Optional;

import org.flightservice.entity.FlightSeat;
import org.flightservice.enums.SeatClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightSeatRepository extends JpaRepository<FlightSeat, Long>{

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE FlightSeat s SET s.availableSeats = s.availableSeats - 1 " +
            "WHERE s.flight.id = :flightId AND s.seatClass = :seatClass AND s.availableSeats > 0")
    int decrementIfAvailable(@Param("flightId") Long flightId, @Param("seatClass") SeatClass seatClass);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE FlightSeat s SET s.availableSeats = s.availableSeats + 1 WHERE s.id = :flightSeatId")
    int increment(@Param("flightSeatId") Long flightSeatId);
    
    Optional<FlightSeat> findByFlightIdAndSeatClass(Long flightId, SeatClass seatClass);

}
