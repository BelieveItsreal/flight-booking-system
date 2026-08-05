package org.bookingservice.service;

import java.util.List;

import org.bookingservice.dto.BookingRequestDTO;
import org.bookingservice.dto.BookingResponseDTO;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO request);
    List<BookingResponseDTO> getAllBooking();
    BookingResponseDTO getBookingById(Long id);
    BookingResponseDTO cancelBooking(Long id);
}
