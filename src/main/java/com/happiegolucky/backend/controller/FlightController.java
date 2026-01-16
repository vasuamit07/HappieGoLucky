package com.happiegolucky.backend.controller;

import com.happiegolucky.backend.dto.FlightDTO;
import com.happiegolucky.backend.service.FlightAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {
    private final FlightAggregatorService aggregatorService;

    @GetMapping("/search")
    public List<FlightDTO> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String departureDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(defaultValue = "1") int adults) { // Added adults here

        // Change the name to getAllFlights and add the 5th parameter (adults)
        return aggregatorService.getAllFlights(from, to, departureDate, returnDate, adults);
    }
}