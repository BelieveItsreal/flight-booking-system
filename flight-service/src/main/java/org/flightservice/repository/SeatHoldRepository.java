package org.flightservice.repository;

import org.flightservice.entity.SeatHold;
import org.flightservice.enums.HoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long>{
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SeatHold h SET h.status = :newStatus WHERE h.id = :id AND h.status = :expectedStatus")
    int updateStatusIfCurrent(@Param("id") Long id,
                              @Param("expectedStatus")HoldStatus expectedStatus,
                              @Param("newStatus") HoldStatus newStatus);
    List<SeatHold> findByStatusAndReservedAtBefore(HoldStatus status, LocalDateTime cutoff);
}
