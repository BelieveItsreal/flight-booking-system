package org.flightservice.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.flightservice.dto.BookingRequestDTO;
import org.flightservice.dto.BookingResponseDTO;
import org.flightservice.entity.Booking;
import org.flightservice.entity.Flight;
import org.flightservice.entity.FlightSeat;
import org.flightservice.entity.User;
import org.flightservice.enums.BookingStatus;
import org.flightservice.enums.Role;
import org.flightservice.enums.SeatClass;
import org.flightservice.exception.BookingNotFoundException;
import org.flightservice.exception.FlightNotFoundException;
import org.flightservice.exception.SeatClassNotFoundException;
import org.flightservice.mapper.BookingMapper;
import org.flightservice.repository.BookingRepository;
import org.flightservice.repository.FlightRepository;
import org.flightservice.repository.FlightSeatRepository;
import org.flightservice.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    
    @Mock private BookingRepository bookingRepository;
    @Mock private FlightRepository flightRepository;
    @Mock private FlightSeatRepository flightSeatRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Flight flight;
    private FlightSeat seat;
    private User user;
    private User adminUser;
    private BookingRequestDTO request;
    private Booking booking;
    private BookingResponseDTO responseDTO;

    @BeforeEach
    void setUp(){
        flight = new Flight();
        flight.setId(1L);
        flight.setFlightNumber("AT101");
        flight.setDepartureTime(LocalDateTime.now().plusDays(2));
        
        seat = new FlightSeat();
        seat.setId(1L);
        seat.setSeatClass(SeatClass.ECONOMY);
        seat.setAvailableSeats(5);
        seat.setPrice(200.0);

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setRole(Role.USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setName("Admin");
        adminUser.setEmail("admin@gmail.com");
        adminUser.setRole(Role.ADMIN);

        request = new BookingRequestDTO(1L, SeatClass.ECONOMY, "AB123456");

        booking = new Booking();
        booking.setId(1L);
        booking.setFlight(flight);
        booking.setFlightSeat(seat);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);

        responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(1L);
        responseDTO.setStatus(BookingStatus.CONFIRMED);
    }

    @Test
    void createBooking_sucess(){
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(flightSeatRepository.findByFlightIdAndSeatClassForUpdate(1L,SeatClass.ECONOMY)).thenReturn(Optional.of(seat));
        when(bookingRepository.save(any(Booking .class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.createBooking(request);

        assertThat(result).isNotNull();
        assertThat(result.getBookingId()).isEqualTo(1L);
        assertThat(seat.getAvailableSeats()).isEqualTo(4);
        verify(flightSeatRepository).save(seat);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_flightNotFound_throwsException(){
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->bookingService.createBooking(request))
            .isInstanceOf(FlightNotFoundException.class)
            .hasMessage("Flight not found with id: "+request.getFlightId());
    }

    @Test
    void createBooking_seatclassNotFound_throwsException(){
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(flightSeatRepository.findByFlightIdAndSeatClassForUpdate(1L, SeatClass.ECONOMY)).thenReturn(Optional.empty());
        assertThatThrownBy(()-> bookingService.createBooking(request))
            .isInstanceOfAny(SeatClassNotFoundException.class)
            .hasMessage("Seat class not available");
    }

    @Test
    void createBooking_noAvailableSeats_throwsException(){
        seat.setAvailableSeats(0);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(flightSeatRepository.findByFlightIdAndSeatClassForUpdate(1L, SeatClass.ECONOMY)).thenReturn(Optional.of(seat));

        assertThatThrownBy(() -> bookingService.createBooking(request))
            .isInstanceOf(SeatClassNotFoundException.class)
            .hasMessageContaining("seat is not available");
    }

    @Test
    void getAllBooking_asAdmin_returnsAllBookings(){
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        List<BookingResponseDTO> result = bookingService.getAllBooking();

        assertThat(result).hasSize(1);
        verify(bookingRepository).findAll();
        verify(bookingRepository, never()).findByUserId(any());
    }

    @Test
    void getAllBooking_asUser_returnOnlyOwnBookings(){
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        List<BookingResponseDTO> result = bookingService.getAllBooking();

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByUserId(1L);
        verify(bookingRepository, never()).findAll();
    }

    @Test
    void getBookingById_asAdmin_canViewBooking(){
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.getBookingById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getBookingById_asOwner_canViewOwnBooking(){
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.getBookingById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getAllBooking_asUser_returnOnlyOwnBooking(){
        User otherUser = new User();
        otherUser.setId(99L);
        otherUser.setRole(Role.USER);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(otherUser);
        
        assertThatThrownBy(()-> bookingService.getBookingById(1L))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("You can only view your own bookings");
    }

    @Test
    void cancelBooking_asOwner_success(){
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        bookingService.cancelBooking(1L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(seat.getAvailableSeats()).isEqualTo(6);
        verify(flightSeatRepository).save(seat);
    }

    @Test
    void cancelBooking_asAdmin_sucess(){
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        bookingService.cancelBooking(1L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_asOtherUser_throwsAccessDeniedException(){
        User otherUser = new User();
        otherUser.setId(99L);
        otherUser.setRole(Role.USER);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(otherUser);
        assertThatThrownBy(()-> bookingService.cancelBooking(1L))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("You can only cancel your own bookings");
    }

    @Test
    void cancelBooking_flightAlreadyDeparted_throwsException() {
        flight.setDepartureTime(LocalDateTime.now().minusDays(1));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThatThrownBy(() -> bookingService.cancelBooking(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Can not cancel booking, FLight is already Departed");
    }
    
    @Test
    void cancelBooking_notFound_throwsException() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancelBooking(99L))
            .isInstanceOf(BookingNotFoundException.class);
    }

}