package org.flightservice.grpc;

import io.grpc.Status;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.flightBooking.proto.*;
import org.flightservice.entity.Flight;
import org.flightservice.entity.SeatHold;
import org.flightservice.entity.FlightSeat;
import org.flightservice.enums.SeatClass;
import org.flightservice.exception.SeatClassNotFoundException;
import org.flightservice.exception.SeatUnavailableException;
import org.flightservice.repository.FlightRepository;
import org.flightservice.service.FlightSeatService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.time.ZoneId;

@GrpcService
public class FlightSeatGrpcService extends FlightSeatServiceGrpc.FlightSeatServiceImplBase{
    private final FlightSeatService flightService;
    private final FlightRepository flightRepository;
    public FlightSeatGrpcService(FlightSeatService flightService, FlightRepository flightRepository){
        this.flightService = flightService;
        this.flightRepository = flightRepository;
    }
    @Override
    public void reserveSeat(ReserveSeatRequest request, StreamObserver<ReserveSeatResponse> responseObserver){
        try {
            SeatClass seatClass = toDomainSeatClass(request.getSeatClass());
            SeatHold hold = flightService.reserveSeat(request.getFlightId(), seatClass);
            responseObserver.onNext(ReserveSeatResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Seat Reserved")
                    .setPriceAtBooking(hold.getPrice())
                    .setFlightSeatId(hold.getFlightSeatId())
                    .setHoldId(hold.getId())
                    .build());
        } catch (SeatClassNotFoundException | SeatUnavailableException ex){
            responseObserver.onNext(ReserveSeatResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void confirmSeat(ConfirmSeatRequest request, StreamObserver<ConfirmSeatResponse> responseObserver){
        try {
            flightService.confirmSeat(request.getHoldId());
            responseObserver.onNext(ConfirmSeatResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Seat COnfirmed")
                    .build());
        } catch (SeatClassNotFoundException ex){
            responseObserver.onNext(ConfirmSeatResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void releaseSeat(ReleaseSeatRequest request, StreamObserver<ReleaseSeatResponse> responseObserver){
        try {
            flightService.releaseSeat(request.getFlightSeatId());
            responseObserver.onNext(ReleaseSeatResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Seat Released")
                    .build());
        } catch (SeatClassNotFoundException ex){
            responseObserver.onNext(ReleaseSeatResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void releaseHold(ReleaseHoldRequest request, StreamObserver<ReleaseHoldResponse> responseObserver){
        try {
            flightService.releaseHold(request.getHoldId());
            responseObserver.onNext(ReleaseHoldResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Seat Hold Released")
                    .build());
        } catch (SeatClassNotFoundException ex){
            responseObserver.onNext(ReleaseHoldResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getFlight(GetFlightRequest request, StreamObserver<FlightDetails> responseObserver){
        Flight flight = flightRepository.findById(request.getFlightId()).orElse(null);
        if (flight == null){
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Flight not found with id: " + request.getFlightId())
                    .asRuntimeException());
            return;
        }
        responseObserver.onNext(FlightDetails.newBuilder()
                .setId(flight.getId())
                .setFlightNumber(flight.getFlightNumber())
                .setSourceCode(flight.getSourceCode())
                .setSourceCity(flight.getSourceCity())
                .setDestinationCode(flight.getDestinationCode())
                .setDestinationCity(flight.getDestinationCity())
                .setDepartureTimeEpochMillis(toEpochMillis(flight.getDepartureTime()))
                .setArrivalTimeEpochMillis(toEpochMillis(flight.getArrivalTime()))
                .build());
        responseObserver.onCompleted();
    }

    private long toEpochMillis(LocalDateTime dateTime){
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private SeatClass toDomainSeatClass(org.flightBooking.proto.SeatClass protoSeatClass){
        return switch (protoSeatClass){
            case ECONOMY -> SeatClass.ECONOMY;
            case BUSINESS -> SeatClass.BUSINESS;
            default -> throw new IllegalArgumentException("Unspecified seat class");
        };
    }
}
