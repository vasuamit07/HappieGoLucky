package com.happiegolucky.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity // Use jakarta.persistence.Entity
@Table(name = "flights")
@Data // Lombok automatically creates Getters/Setters
public class FlightEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String airline;
    private String flightNumber;
    private BigDecimal price;

    private String origin; // Added Origin (e.g., JFK)
    private String destination; // Added Destination (e.g., LHR)

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}