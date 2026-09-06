package org.bookingservice.producer;

import org.bookingservice.event.BookingConfirmedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingEventPublisher {
    private static final String BOOKING_CONFIRMED_TOPIC = "booking-confirmed";
    private final KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;
    public BookingEventPublisher(KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publishBookingConfirmed(BookingConfirmedEvent event){
        kafkaTemplate.send(BOOKING_CONFIRMED_TOPIC, event.getBookingId().toString(), event);
    }
}
