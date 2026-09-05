package org.flightservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flightservice.enums.HoldStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat_holds")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SeatHold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long flightSeatId;
    private Double price;
    @Enumerated(EnumType.STRING)
    private HoldStatus status;
    private LocalDateTime reservedAt;
}
