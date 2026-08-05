package org.bookingservice.mapper;

import org.bookingservice.dto.BookingResponseDTO;
import org.bookingservice.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingResponseDTO toDto(Booking booking){
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setFlightId(booking.getFlightId());
        dto.setFlightSeatId(booking.getFlightSeatId());
        dto.setUserId(booking.getUserId());
        dto.setSeatClass(booking.getSeatClass());
        dto.setPassportNumber(booking.getPassportNumber());
        dto.setPriceAtBooking(booking.getPriceAtBooking());
        dto.setBookingTime(booking.getBookingTime());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
