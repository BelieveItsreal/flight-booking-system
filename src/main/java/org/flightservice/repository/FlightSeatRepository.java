package org.flightservice.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.flightservice.entity.FlightSeat;
import org.flightservice.enums.SeatClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightSeatRepository extends JpaRepository<FlightSeat, Long>{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM FlightSeat s WHERE s.flight.id = :flightId AND s.seatClass = :seatClass")
    Optional<FlightSeat> findByFlightIdAndSeatClassForUpdate(@Param("flightId") Long flightId, @Param("seatClass") SeatClass seatClass);
}
