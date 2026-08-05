package org.bookingservice.controller;

import java.util.List;

import org.bookingservice.dto.BookingRequestDTO;
import org.bookingservice.dto.BookingResponseDTO;
import org.bookingservice.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingResponseDTO>createBooking(@Valid @RequestBody BookingRequestDTO request) {
        BookingResponseDTO bookingDetail = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingDetail);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingResponseDTO>> getAllBooking() {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllBooking());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookingById(id));
    }

    @PutMapping("cancel/{id}")
    public ResponseEntity<BookingResponseDTO>cancelBooking(@PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.cancelBooking(id));
    }

}
