package org.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bookingservice.enums.OutboxStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String topic;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
