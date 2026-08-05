package org.bookingservice.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.bookingservice.config.AuthenticatedUser;
import org.bookingservice.dto.BookingRequestDTO;
import org.bookingservice.dto.BookingResponseDTO;
import org.bookingservice.entity.Booking;
import org.bookingservice.enums.BookingStatus;
import org.bookingservice.enums.Role;
import org.bookingservice.exception.BookingNotFoundException;
import org.bookingservice.mapper.BookingMapper;
import org.bookingservice.repository.BookingRepository;
import org.bookingservice.service.BookingService;
import org.bookingservice.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BookingServiceImpl implements BookingService{

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private SecurityUtils securityUtils;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        AuthenticatedUser currentUser = securityUtils.getCurrentUser();

        // Seat availability/locking and pricing lived in flight-service's FlightSeatRepository.
        // For Phase 0 this just records the request - reserving the seat and pricing the
        // booking move to a synchronous ReserveSeat gRPC call to flight-service in a later phase.
        Booking booking = new Booking();
        booking.setFlightId(request.getFlightId());
        booking.setUserId(currentUser.userId());
        booking.setSeatClass(request.getSeatClass());
        booking.setPassportNumber(request.getPassportNumber());
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingMapper.toDto(bookingRepository.save(booking));
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
    public BookingResponseDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: "+id));
        AuthenticatedUser currentUser = securityUtils.getCurrentUser();
        if (currentUser.role() != Role.ADMIN && !booking.getUserId().equals(currentUser.userId())) {
            throw new AccessDeniedException("You can only cancel your own bookings");
        }
        // Guarding against cancelling an already-departed flight required a flight-service
        // lookup (booking.getFlight().getDepartureTime()). Deferred to the same later phase
        // as ReserveSeat/ReleaseSeat wiring.
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }
}
