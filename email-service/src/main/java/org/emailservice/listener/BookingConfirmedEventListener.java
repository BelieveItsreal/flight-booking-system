package org.emailservice.listener;

import org.emailservice.dto.BookingConfirmationRequest;
import org.emailservice.event.BookingConfirmedEvent;
import org.emailservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingConfirmedEventListener {
    private final EmailService emailService;
    public BookingConfirmedEventListener(EmailService emailService){
        this.emailService = emailService;
    }
    @KafkaListener(topics = "booking-confirmed", groupId = "email-service")
    public void handleBookingConfirmed(BookingConfirmedEvent event){
        BookingConfirmationRequest request = new BookingConfirmationRequest(
                event.getUserEmail(),
                event.getBookingId(),
                event.getFlightNumber(),
                event.getSeatClass(),
                event.getBookingTime(),
                event.getDepartureTime(),
                event.getPriceAtBooking()
        );
        emailService.sendBookingConfirmation(request);
    }
}
