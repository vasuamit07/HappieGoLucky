package com.happiegolucky.backend.controller;

import com.happiegolucky.backend.api.controller.SearchApi;
import com.happiegolucky.backend.api.model.Flight;
import com.happiegolucky.backend.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController // This remains the public API endpoint
@RequiredArgsConstructor // Lombok to inject the service
public class FlightController implements SearchApi {

    // Inject the new Service layer
    private final FlightService flightService;

    @Override
    public ResponseEntity<List<Flight>> searchFlights(String from, String to, LocalDate date) {

        // This is the core logic: Controller calls Service, Service calls Repository.
        List<Flight> flights = flightService.searchFlights(from, to, date);

        return ResponseEntity.ok(flights);
    }
}
