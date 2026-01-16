package com.happiegolucky.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AirportService {
    private List<Map<String, String>> airports = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("static/data/airports.json").getInputStream();
            airports = mapper.readValue(is, new TypeReference<List<Map<String, String>>>() {
            });
            System.out.println("Loaded " + airports.size() + " airports for prediction.");
        } catch (Exception e) {
            System.err.println("Could not load airports: " + e.getMessage());
        }
    }

    public List<Map<String, String>> searchAirports(String query) {
        String lowerQuery = query.toLowerCase();
        return airports.stream()
                .filter(a -> a.get("Airport name").toLowerCase().contains(lowerQuery) ||
                        a.get("IATA").toLowerCase().contains(lowerQuery) ||
                        a.get("City").toLowerCase().contains(lowerQuery))
                .sorted((a, b) -> {
                    // Priority 1: Exact IATA match
                    if (a.get("IATA").toLowerCase().equals(lowerQuery)) return -1;
                    if (b.get("IATA").toLowerCase().equals(lowerQuery)) return 1;
                    // Priority 2: Starts with query
                    if (a.get("Airport name").toLowerCase().startsWith(lowerQuery)) return -1;
                    if (b.get("Airport name").toLowerCase().startsWith(lowerQuery)) return 1;
                    return 0;
                })
                .limit(8)
                .collect(Collectors.toList());
    }
    public String getAirportInfo(String iata) {
        return airports.stream()
                .filter(a -> a.get("IATA").equalsIgnoreCase(iata))
                .map(a -> a.get("Information"))
                .findFirst()
                .orElse("https://www.google.com/search?q=" + iata + "+airport+guide");
    }
}