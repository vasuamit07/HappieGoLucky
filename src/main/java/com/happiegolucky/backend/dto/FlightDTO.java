package com.happiegolucky.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FlightDTO {
    private String airline;
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureDate;   // e.g., "2026-02-15T10:00:00"
    private String returnDate;
    private BigDecimal price;
    private String provider;        // e.g., "Amadeus GDS"
    private String status;          // e.g., "AVAILABLE", "ONLY 2 LEFT"
    private String bookingUrl;

    // --- NEW FIELDS ---
    private String duration;        // e.g., "14h 30m"
    private int stopCount;          // e.g., 0, 1, 2
    private List<String> stopAirports; // e.g., ["LHR"] for a stop in London
}