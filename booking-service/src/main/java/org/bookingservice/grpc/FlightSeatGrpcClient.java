package org.bookingservice.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.bookingservice.enums.SeatClass;
import org.flightBooking.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FlightSeatGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(FlightSeatGrpcClient.class);

    @GrpcClient("flight-service")
    private FlightSeatServiceGrpc.FlightSeatServiceBlockingStub flightSeatStub;
    public ReserveSeatResponse reserveSeat(Long flightId, SeatClass seatClass){
        return flightSeatStub.reserveSeat(ReserveSeatRequest.newBuilder()
                .setFlightId(flightId)
                .setSeatClass(toProtoSeatClass(seatClass))
                .build());
    }

    public void confirmSeat(Long holdId){
        ConfirmSeatResponse response = flightSeatStub.confirmSeat(ConfirmSeatRequest.newBuilder()
                .setHoldId(holdId)
                .build());
        if (!response.getSuccess()){
            log.error("Failed to confirm seat hold {} : {}", holdId, response.getMessage());
        }
    }

    public void releaseHold(Long holdId){
        ReleaseHoldResponse response = flightSeatStub.releaseHold(ReleaseHoldRequest.newBuilder()
                .setHoldId(holdId)
                .build());
        if (!response.getSuccess()){
            log.error("Failed to confirm seat hold {} : {}", holdId, response.getMessage());
        }
    }

    public void releaseSeat(Long flightSeatId){
        ReleaseSeatResponse response = flightSeatStub.releaseSeat(ReleaseSeatRequest.newBuilder()
                .setFlightSeatId(flightSeatId)
                .build());
        if (!response.getSuccess()) {
            log.error("Failed to release flight seat {}: {}", flightSeatId, response.getMessage());
        }
    }

    public FlightDetails getFlight(Long flightId){
        return flightSeatStub.getFlight(GetFlightRequest.newBuilder()
                .setFlightId(flightId)
                .build());
    }

    private org.flightBooking.proto.SeatClass toProtoSeatClass(SeatClass seatClass){
        return switch (seatClass){
            case ECONOMY ->   org.flightBooking.proto.SeatClass.ECONOMY;
            case BUSINESS -> org.flightBooking.proto.SeatClass.BUSINESS;
        };
    }
}
