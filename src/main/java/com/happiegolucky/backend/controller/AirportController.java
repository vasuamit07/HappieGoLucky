package com.happiegolucky.backend.controller;

import com.happiegolucky.backend.service.AirportService; // Import your service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/airports")
@CrossOrigin(origins = "*")
public class AirportController {

    @Autowired
    private AirportService airportService; // Inject the service that loads your JSON

    @GetMapping("/search")
    public List<Map<String, String>> search(@RequestParam String query) {
        // This now uses your 7,000+ airport JSON file!
        return airportService.searchAirports(query);
    }
}