package org.flightservice.repository;

import org.flightservice.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    Page<Flight>findBySourceCode(String sourceCode, Pageable pageable);
    Page<Flight>findByDestinationCode(String destinationCode, Pageable pageable);
    Page<Flight>findBySourceCodeAndDestinationCode(String sourceCode, String destinationCode, Pageable pageable);
    
    @Query("SELECT DISTINCT f FROM Flight f JOIN f.seatClasses s " +
       "WHERE (:minPrice IS NULL OR s.price >= :minPrice) " +
       "AND (:maxPrice IS NULL OR s.price <= :maxPrice)")
    Page<Flight> findFlightsBySeatPriceRange(@Param("minPrice") Double minPrice, 
    @Param("maxPrice") Double maxPrice,
    Pageable pageable);
}
