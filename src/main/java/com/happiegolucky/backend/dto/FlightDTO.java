package com.happiegolucky.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {
    private UUID id;
    private String airline;
    private String flightNumber;
    private String origin;
    private String destination;
    private BigDecimal price;
    private String departureDate;
    private String returnDate; // Restored
    private String bookingUrl; // Deep-link to booking
    private String provider;
    private String status;     // Restored (e.g., "AVAILABLE" or "ONLY 2 LEFT")
}