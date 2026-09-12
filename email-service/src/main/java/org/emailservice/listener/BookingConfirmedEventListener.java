package org.emailservice.listener;

import org.emailservice.dto.BookingConfirmationRequest;
import org.emailservice.event.BookingConfirmedEvent;
import org.emailservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class BookingConfirmedEventListener {

    private static final Logger log = LoggerFactory.getLogger(BookingConfirmedEventListener.class);

    private final EmailService emailService;

    public BookingConfirmedEventListener(EmailService emailService){
        this.emailService = emailService;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
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

    @DltHandler
    public void handleDlt(BookingConfirmedEvent event){
        log.error("Booking confirmation email permanently failed after all retries for booking {}", event.getBookingId());
    }
}
