package org.bookingservice.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.bookingservice.config.AuthenticatedUser;
import org.bookingservice.dto.BookingRequestDTO;
import org.bookingservice.dto.BookingResponseDTO;
import org.bookingservice.entity.Booking;
import org.bookingservice.enums.BookingStatus;
import org.bookingservice.enums.Role;
import org.bookingservice.enums.SeatClass;
import org.bookingservice.exception.BookingNotFoundException;
import org.bookingservice.mapper.BookingMapper;
import org.bookingservice.repository.BookingRepository;
import org.bookingservice.util.SecurityUtils;
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
    @Mock private BookingMapper bookingMapper;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private AuthenticatedUser user;
    private AuthenticatedUser adminUser;
    private BookingRequestDTO request;
    private Booking booking;
    private BookingResponseDTO responseDTO;

    @BeforeEach
    void setUp(){
        user = new AuthenticatedUser(1L, "john@gmail.com", Role.USER);
        adminUser = new AuthenticatedUser(2L, "admin@gmail.com", Role.ADMIN);

        request = new BookingRequestDTO(1L, SeatClass.ECONOMY, "AB123456");

        booking = new Booking();
        booking.setId(1L);
        booking.setFlightId(1L);
        booking.setUserId(1L);
        booking.setStatus(BookingStatus.CONFIRMED);

        responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(1L);
        responseDTO.setStatus(BookingStatus.CONFIRMED);
    }

    @Test
    void createBooking_success(){
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.createBooking(request);

        assertThat(result).isNotNull();
        assertThat(result.getBookingId()).isEqualTo(1L);
        verify(bookingRepository).save(any(Booking.class));
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
    void getBookingById_asOtherUser_throwsAccessDeniedException(){
        AuthenticatedUser otherUser = new AuthenticatedUser(99L, "other@gmail.com", Role.USER);
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
    }

    @Test
    void cancelBooking_asAdmin_success(){
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(adminUser);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDTO);

        bookingService.cancelBooking(1L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_asOtherUser_throwsAccessDeniedException(){
        AuthenticatedUser otherUser = new AuthenticatedUser(99L, "other@gmail.com", Role.USER);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityUtils.getCurrentUser()).thenReturn(otherUser);

        assertThatThrownBy(()-> bookingService.cancelBooking(1L))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("You can only cancel your own bookings");
    }

    @Test
    void cancelBooking_notFound_throwsException() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancelBooking(99L))
            .isInstanceOf(BookingNotFoundException.class);
    }

}
