package com.happiegolucky.backend.controller;

import com.happiegolucky.backend.entity.FlightEntity;
import com.happiegolucky.backend.repository.FlightRepository; // Ensure this is imported
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor // This automatically links flightRepository
public class FlightController {

    private final FlightRepository flightRepository; // This line was missing or not linked

    @GetMapping("/search")
    public List<FlightEntity> searchFlights(@RequestParam String from, @RequestParam String to) {
        return flightRepository.findByOriginAndDestination(from, to);
    }

    @GetMapping("/status/{code}")
    public ResponseEntity<FlightEntity> getFlightStatus(@PathVariable String code) {
        return flightRepository.findByFlightNumberIgnoreCase(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}