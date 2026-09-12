package org.bookingservice.producer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bookingservice.entity.OutboxEvent;
import org.bookingservice.enums.OutboxStatus;
import org.bookingservice.event.BookingConfirmedEvent;
import org.bookingservice.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OutboxRelay {
    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);
    private static final int BATCH_SIZE = 50;
    private static final long SEND_TIMEOUT_SECONDS = 10;
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public OutboxRelay(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate,
                       ObjectMapper objectMapper){
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    @Scheduled(fixedDelay = 5000)
    public void relayPendingEvents(){
        List<OutboxEvent> pending = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING, PageRequest.of(0, BATCH_SIZE));
        for (OutboxEvent event : pending){
            relayOne(event);
        }
    }
    private void relayOne(OutboxEvent event){
        try {
            BookingConfirmedEvent payload = objectMapper.readValue(event.getPayload(), BookingConfirmedEvent.class);
            kafkaTemplate.send(event.getTopic(), event.getAggregateId(), payload)
                    .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            markSent(event.getId());
        }catch (Exception ex){
            log.error("Failed to relay outbox event {} (booking {}); will retry next poll",
            event.getId(), event.getAggregateId(), ex);
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSent(Long outBoxEventId){
        outboxEventRepository.findById(outBoxEventId).ifPresent(event -> {
            event.setStatus(OutboxStatus.SENT);
            event.setPublishedAt(LocalDateTime.now());
            outboxEventRepository.save(event);
        });
    }
}
