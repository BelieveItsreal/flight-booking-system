package org.bookingservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookingservice.entity.OutboxEvent;
import org.bookingservice.enums.OutboxStatus;
import org.bookingservice.event.BookingConfirmedEvent;
import org.bookingservice.repository.OutboxEventRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OutboxEventWriter {
    private static final String BOOKING_CONFIRMED_TOPIC = "booking-confirmed";
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventWriter(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper){
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }
    public void savedBookingConfirmed(BookingConfirmedEvent event){
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize BookingConfirmedEvent for booking " + event.getBookingId(), ex);
        }
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType("Booking");
        outboxEvent.setAggregateId(event.getBookingId().toString());
        outboxEvent.setEventType("Booking Confirmed");
        outboxEvent.setTopic(BOOKING_CONFIRMED_TOPIC);
        outboxEvent.setPayload(payload);
        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEventRepository.save(outboxEvent);
    }
}
