package org.flightservice.schedular;

import org.flightservice.service.FlightSeatService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SeatHoldExpiryScheduler {
    private final FlightSeatService flightSeatService;
    public SeatHoldExpiryScheduler(FlightSeatService flightSeatService){
        this.flightSeatService = flightSeatService;
    }
    @Scheduled(fixedRate = 60000)
    public void expireStaleHolds(){
        flightSeatService.expireStaleHolds();
    }
}
