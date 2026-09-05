package org.bookingservice.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.bookingservice.config.AuthenticatedUser;
import org.bookingservice.dto.BookingRequestDTO;
import org.bookingservice.dto.BookingResponseDTO;
import org.bookingservice.entity.Booking;
import org.bookingservice.enums.BookingStatus;
import org.bookingservice.enums.Role;
import org.bookingservice.event.BookingConfirmedEvent;
import org.bookingservice.exception.BookingNotFoundException;
import org.bookingservice.exception.SeatUnavailableException;
import org.bookingservice.grpc.FlightSeatGrpcClient;
import org.bookingservice.mapper.BookingMapper;
import org.bookingservice.producer.BookingEventPublisher;
import org.bookingservice.repository.BookingRepository;
import org.bookingservice.service.BookingService;
import org.bookingservice.util.SecurityUtils;
import org.flightBooking.proto.FlightDetails;
import org.flightBooking.proto.ReserveSeatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BookingServiceImpl implements BookingService{

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final SecurityUtils securityUtils;
    private final BookingEventPublisher bookingEventPublisher;
    private final FlightSeatGrpcClient flightSeatGrpcClient;

    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper,
                              SecurityUtils securityUtils, BookingEventPublisher bookingEventPublisher,
                              FlightSeatGrpcClient flightSeatGrpcClient) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.securityUtils = securityUtils;
        this.bookingEventPublisher = bookingEventPublisher;
        this.flightSeatGrpcClient = flightSeatGrpcClient;
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        AuthenticatedUser currentUser = securityUtils.getCurrentUser();
        FlightDetails flight = flightSeatGrpcClient.getFlight(request.getFlightId());
        ReserveSeatResponse reservation = flightSeatGrpcClient.reserveSeat(request.getFlightId(), request.getSeatClass());
        if (!reservation.getSuccess()){
            throw new SeatUnavailableException(reservation.getMessage());
        }
        Booking booking = new Booking();
        booking.setFlightId(request.getFlightId());
        booking.setFlightSeatId(reservation.getFlightSeatId());
        booking.setUserId(currentUser.userId());
        booking.setSeatClass(booking.getSeatClass());
        booking.setPassportNumber(booking.getPassportNumber());
        booking.setBookingTime(booking.getBookingTime());
        booking.setPriceAtBooking(booking.getPriceAtBooking());
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking;
        try {
            savedBooking = bookingRepository.save(booking);
        }catch (RuntimeException ex){
            flightSeatGrpcClient.releaseSeat(reservation.getFlightSeatId());
            throw ex;
        }
        try {
            flightSeatGrpcClient.confirmSeat(reservation.getHoldId());
        } catch (RuntimeException ex) {
            bookingRepository.delete(savedBooking);
            try {
                flightSeatGrpcClient.releaseHold(reservation.getHoldId());
            } catch (RuntimeException releaseEx) {
                log.error("Failed to release seat hold {} after confirm failure", reservation.getHoldId(), releaseEx);
            }
            throw ex;
        }
        LocalDateTime departureTime = Instant.ofEpochMilli(flight.getDepartureTimeEpochMillis())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        BookingConfirmedEvent event = new BookingConfirmedEvent(
                UUID.randomUUID().toString(),
                savedBooking.getId(),
                currentUser.userId(),
                currentUser.email(),
                savedBooking.getFlightId(),
                flight.getFlightNumber(),
                savedBooking.getSeatClass().name(),
                savedBooking.getBookingTime(),
                departureTime,
                savedBooking.getPriceAtBooking()
        );
        bookingEventPublisher.publishBookingConfirmed(event);
        return bookingMapper.toDto(savedBooking);
    }

    public List<BookingResponseDTO> getAllBooking(){
        AuthenticatedUser currentUser = securityUtils.getCurrentUser();
        if (currentUser.role() == Role.ADMIN) {
            return bookingRepository.findAll().stream().map(bookingMapper::toDto).toList();
        }
        return bookingRepository.findByUserId(currentUser.userId()).stream().map(bookingMapper::toDto).toList();
    }

    public BookingResponseDTO getBookingById(Long id){
        Booking booking = bookingRepository.findById(id).
                orElseThrow(()-> new BookingNotFoundException("Booking not found with the id" +id));
        AuthenticatedUser currentUser = securityUtils.getCurrentUser();
        if (currentUser.role() != Role.ADMIN && !booking.getUserId().equals(currentUser.userId())) {
            throw new AccessDeniedException("You can only view your own bookings");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: "+id));
        AuthenticatedUser currentUser = securityUtils.getCurrentUser();
        if (currentUser.role() != Role.ADMIN && !booking.getUserId().equals(currentUser.userId())) {
            throw new AccessDeniedException("You can only cancel your own bookings");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);
        flightSeatGrpcClient.releaseSeat(booking.getFlightSeatId());
        return bookingMapper.toDto(savedBooking);
    }
}