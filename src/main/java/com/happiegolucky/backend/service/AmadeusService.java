package com.happiegolucky.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiegolucky.backend.dto.FlightDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration; // Import for duration parsing
import java.util.*;

@Service
public class AmadeusService {

    @Value("${amadeus.api.key}")
    private String apiKey;

    @Value("${amadeus.api.secret}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    private String accessToken;
    private long tokenExpiryTime;

    // --- 1. Authentication (Same as before) ---
    private void authenticate() {
        if (System.currentTimeMillis() < tokenExpiryTime) return;

        String url = "https://test.api.amadeus.com/v1/security/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + apiSecret;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
            JsonNode root = mapper.readTree(response.getBody());
            this.accessToken = root.get("access_token").asText();
            this.tokenExpiryTime = System.currentTimeMillis() + (root.get("expires_in").asLong() * 1000);
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate with Amadeus", e);
        }
    }

    // --- 2. Real Flight Search ---
    public List<FlightDTO> getRealFlights(String from, String to, String depDate, String retDate, int adults) {
        authenticate();

        String url = "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=" + from
                + "&destinationLocationCode=" + to
                + "&departureDate=" + depDate
                + "&adults=" + adults
                + "&max=15&currencyCode=USD";

        if (retDate != null && !retDate.isEmpty()) {
            url += "&returnDate=" + retDate;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            return parseAmadeusResponse(response.getBody());
        } catch (Exception e) {
            System.err.println("Amadeus Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // --- 3. Parsing Logic (UPDATED) ---
    private List<FlightDTO> parseAmadeusResponse(String json) throws Exception {
        List<FlightDTO> flights = new ArrayList<>();
        JsonNode root = mapper.readTree(json);
        JsonNode data = root.get("data");

        if (data != null && data.isArray()) {
            for (JsonNode offer : data) {
                FlightDTO dto = new FlightDTO();

                // 1. Airline & Flight Number
                JsonNode itinerary = offer.get("itineraries").get(0);
                JsonNode firstSegment = itinerary.get("segments").get(0);
                String carrierCode = firstSegment.get("carrierCode").asText();
                String number = firstSegment.get("number").asText();

                dto.setAirline(getAirlineName(carrierCode));
                dto.setFlightNumber(carrierCode + number);

                // 2. Route & Provider
                dto.setOrigin(firstSegment.get("departure").get("iataCode").asText());
                dto.setDestination(itinerary.get("segments").get(itinerary.get("segments").size()-1).get("arrival").get("iataCode").asText());
                dto.setProvider("Amadeus GDS (Live)");

                // 3. Price & Status
                dto.setPrice(new BigDecimal(offer.get("price").get("total").asText()));
                int seats = offer.get("numberOfBookableSeats").asInt();
                dto.setStatus(seats < 5 ? "ONLY " + seats + " LEFT" : "AVAILABLE");

                // --- 4. NEW: Duration Parsing ---
                String isoDuration = itinerary.get("duration").asText(); // Returns PT14H30M
                dto.setDuration(formatDuration(isoDuration));

                // --- 5. NEW: Stops Calculation ---
                int segmentCount = itinerary.get("segments").size();
                dto.setStopCount(segmentCount - 1); // 1 segment = 0 stops

                List<String> stops = new ArrayList<>();
                if (segmentCount > 1) {
                    // Add intermediate airports
                    for(int i=0; i<segmentCount-1; i++) {
                        stops.add(itinerary.get("segments").get(i).get("arrival").get("iataCode").asText());
                    }
                }
                dto.setStopAirports(stops);

                // 6. Dates
                dto.setDepartureDate(firstSegment.get("departure").get("at").asText().split("T")[0]);
                if (offer.get("itineraries").size() > 1) {
                    dto.setReturnDate(offer.get("itineraries").get(1).get("segments").get(0).get("departure").get("at").asText().split("T")[0]);
                } else {
                    dto.setReturnDate("One-Way");
                }

                flights.add(dto);
            }
        }
        return flights;
    }

    // Helper: ISO Duration (PT12H30M) -> Readable (12h 30m)
    private String formatDuration(String isoDuration) {
        try {
            // Remove "PT"
            String clean = isoDuration.replace("PT", "").toLowerCase();
            // Simple replacement for display. Real parsing is complex, this works for 99%
            return clean.replace("h", "h ").replace("m", "m");
        } catch (Exception e) { return isoDuration; }
    }

    // Helper: Map codes to names (Basic list, expand as needed)
    private String getAirlineName(String code) {
        switch (code) {
            case "BA": return "British Airways";
            case "AC": return "Air Canada";
            case "AI": return "Air India";
            case "DL": return "Delta Airlines";
            case "UA": return "United Airlines";
            case "LH": return "Lufthansa";
            case "AF": return "Air France";
            case "EK": return "Emirates";
            case "EY": return "Etihad Airways";
            default: return code + " Airlines";
        }
    }
}