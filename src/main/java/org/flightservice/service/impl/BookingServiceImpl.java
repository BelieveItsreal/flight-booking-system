package org.flightservice.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.flightservice.dto.BookingRequestDTO;
import org.flightservice.dto.BookingResponseDTO;
import org.flightservice.entity.Booking;
import org.flightservice.entity.Flight;
import org.flightservice.entity.FlightSeat;
import org.flightservice.entity.User;
import org.flightservice.enums.BookingStatus;
import org.flightservice.enums.Role;
import org.flightservice.exception.BookingNotFoundException;
import org.flightservice.exception.FlightNotFoundException;
import org.flightservice.exception.SeatClassNotFoundException;
import org.flightservice.mapper.BookingMapper;
import org.flightservice.repository.BookingRepository;
import org.flightservice.repository.FlightRepository;
import org.flightservice.repository.FlightSeatRepository;
import org.flightservice.service.BookingService;
import org.flightservice.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BookingServiceImpl implements BookingService{

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightSeatRepository flightSeatRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private SecurityUtils securityUtils;
    
    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        Flight flight = flightRepository.findById(request.getFlightId())
        .orElseThrow(() ->  new FlightNotFoundException("Flight not found with id: "+request.getFlightId()));

        User user = securityUtils.getCurrentUser();

        FlightSeat seat = flightSeatRepository.findByFlightIdAndSeatClassForUpdate(request.getFlightId(), request.getSeatClass())
        .orElseThrow(()-> new SeatClassNotFoundException("Seat class not available"));

        if (seat.getAvailableSeats() <= 0) {
            throw new SeatClassNotFoundException("seat is not available");
        }

        //building booking entity
        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setFlightSeat(seat);
        booking.setUser(user);
        booking.setSeatClass(request.getSeatClass());
        booking.setPriceAtBooking(seat.getPrice());
        booking.setPassportNumber(request.getPassportNumber());
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        //decrement available seat
        seat.setAvailableSeats(seat.getAvailableSeats()-1);
        flightSeatRepository.save(seat);

        //saving the booking
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    public List<BookingResponseDTO> getAllBooking(){
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return bookingRepository.findAll().stream().map(bookingMapper::toDto).toList();
        }
        return bookingRepository.findByUserId(currentUser.getId()).stream().map(bookingMapper::toDto).toList();
    }

    public BookingResponseDTO getBookingById(Long id){
        Booking booking = bookingRepository.findById(id).
            orElseThrow(()-> new BookingNotFoundException("Booking not found with the id" +id));
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only view your own bookings");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: "+id));
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only cancel your own bookings");
        }
        if (booking.getFlight().getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can not cancel booking, FLight is already Departed");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        FlightSeat seat = booking.getFlightSeat();
        seat.setAvailableSeats(seat.getAvailableSeats()+1);
        flightSeatRepository.save(seat);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }  
}
