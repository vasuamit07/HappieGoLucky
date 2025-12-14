package com.happiegolucky.backend.service;

import com.happiegolucky.backend.api.model.Flight;
import com.happiegolucky.backend.entity.FlightEntity;
import com.happiegolucky.backend.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service // Tells Spring: "I hold the business logic!"
@RequiredArgsConstructor // Lombok for easy dependency injection
public class FlightService {

    // Spring automatically injects the Repository object here
    private final FlightRepository flightRepository;

    /**
     * Business Logic: Finds flights from the database and converts them
     * from the internal database format (Entity) to the public API format (Model).
     */
    public List<Flight> searchFlights(String from, String to, LocalDate date) {

        // 1. Fetch data from the database
        // NOTE: We're ignoring the 'date' parameter for simplicity right now.
        List<FlightEntity> entities = flightRepository.findByOriginAndDestination(from, to);

        // 2. Convert the database Entities into the API Models/DTOs
        return entities.stream()
                .map(this::toApiModel)
                .collect(Collectors.toList());
    }

    /**
     * Private mapping function (from Entity to Model/DTO)
     */
    private Flight toApiModel(FlightEntity entity) {
        Flight model = new Flight();
        model.setId(entity.getId());
        model.setAirline(entity.getAirline());
        model.setFlightNumber(entity.getFlightNumber());
        model.setPrice(entity.getPrice());

        // Convert LocalDateTime to OffsetDateTime (API Model uses this)
        model.setDepartureTime(entity.getDepartureTime().atOffset(java.time.ZoneOffset.UTC));
        model.setArrivalTime(entity.getArrivalTime().atOffset(java.time.ZoneOffset.UTC));

        return model;
    }
}