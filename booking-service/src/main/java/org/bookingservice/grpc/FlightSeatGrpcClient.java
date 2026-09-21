package org.bookingservice.grpc;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.bookingservice.enums.SeatClass;
import org.flightBooking.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FlightSeatGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(FlightSeatGrpcClient.class);
    private static final long CALL_TIMEOUT_SECONDS = 3;
    @GrpcClient("flight-service")
    private FlightSeatServiceGrpc.FlightSeatServiceBlockingStub flightSeatStub;

    @CircuitBreaker(name = "flightService", fallbackMethod = "reserveSeatFallback")
    public ReserveSeatResponse reserveSeat(Long flightId, SeatClass seatClass){
        return stub().reserveSeat(ReserveSeatRequest.newBuilder()
                .setFlightId(flightId)
                .setSeatClass(toProtoSeatClass(seatClass))
                .build());
    }

    private ReserveSeatResponse reserveSeatFallback(Long flightId, SeatClass seatClass, Throwable t){
        log.error("flight-service unavailable while reserving seat for flight {}: {}", flightId, t.toString());
        throw unavailable("Flight service is currently unavailable, please try again later", t);
    }

    @CircuitBreaker(name = "flightService", fallbackMethod = "confirmSeatFallback")
    public void confirmSeat(Long holdId){
        ConfirmSeatResponse response = stub().confirmSeat(ConfirmSeatRequest.newBuilder()
                .setHoldId(holdId)
                .build());
        if (!response.getSuccess()){
            log.error("Failed to confirm seat hold {} : {}", holdId, response.getMessage());
        }
    }

    private void confirmSeatFallback(Long holdId, Throwable t){
        log.error("flight-service unavailable while confirming seat hold {}: {}", holdId, t.toString());
    }

    @CircuitBreaker(name = "flightService", fallbackMethod = "releaseHoldFallback")
    public void releaseHold(Long holdId){
        ReleaseHoldResponse response = stub().releaseHold(ReleaseHoldRequest.newBuilder()
                .setHoldId(holdId)
                .build());
        if (!response.getSuccess()){
            log.error("Failed to confirm seat hold {} : {}", holdId, response.getMessage());
        }
    }

    private void releaseHoldFallback(Long holdId, Throwable t){
        log.error("flight-service unavailable while releasing seat hold {}: {}", holdId, t.toString());
    }

    @CircuitBreaker(name = "flightService", fallbackMethod = "releaseSeatFallback")
    public void releaseSeat(Long flightSeatId){
        ReleaseSeatResponse response = stub().releaseSeat(ReleaseSeatRequest.newBuilder()
                .setFlightSeatId(flightSeatId)
                .build());
        if (!response.getSuccess()) {
            log.error("Failed to release flight seat {}: {}", flightSeatId, response.getMessage());
        }
    }

    private void releaseSeatFallback(Long flightSeatId, Throwable t){
        log.error("flight-service unavailable while releasing flight seat {}: {}", flightSeatId, t.toString());
    }

    @CircuitBreaker(name = "flightService", fallbackMethod = "getFlightFallback")
    public FlightDetails getFlight(Long flightId){
        return stub().getFlight(GetFlightRequest.newBuilder()
                .setFlightId(flightId)
                .build());
    }

    private FlightDetails getFlightFallback(Long flightId, Throwable t){
        log.error("flight-service unavailable while fetching flight {}: {}", flightId, t.toString());
        throw unavailable("Flight service is currently unavailable, please try again later", t);
    }

    private FlightSeatServiceGrpc.FlightSeatServiceBlockingStub stub(){
        return flightSeatStub.withDeadlineAfter(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private StatusRuntimeException unavailable(String message, Throwable cause){
        return Status.UNAVAILABLE.withDescription(message).withCause(cause).asRuntimeException();
    }

    private org.flightBooking.proto.SeatClass toProtoSeatClass(SeatClass seatClass){
        return switch (seatClass){
            case ECONOMY ->   org.flightBooking.proto.SeatClass.ECONOMY;
            case BUSINESS -> org.flightBooking.proto.SeatClass.BUSINESS;
        };
    }
}
