package com.happiegolucky.backend.service;

import com.happiegolucky.backend.dto.FlightDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AmadeusService {

    private final RestTemplate restTemplate;

    @Value("${amadeus.api.key}")
    private String apiKey;

    @Value("${amadeus.api.secret}")
    private String apiSecret;

    private String token = null;

    private void authenticate() {
        String url = "https://test.api.amadeus.com/v1/security/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", apiKey);
        map.add("client_secret", apiSecret);

        Map response = restTemplate.postForObject(url, new HttpEntity<>(map, headers), Map.class);
        this.token = (String) response.get("access_token");
    }

    public List<FlightDTO> getRealFlights(String from, String to, String dep, String ret, int adults) {
        try {
            if (token == null) authenticate();

            String url = String.format("https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=%s&destinationLocationCode=%s&departureDate=%s&adults=%d&max=10",
                    from.toUpperCase(), to.toUpperCase(), dep, adults);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");
            Map dicts = (Map) response.getBody().get("dictionaries");

            List<FlightDTO> results = new ArrayList<>();
            if (data != null) {
                for (Map offer : data) {
                    results.add(mapToDTO(offer, dicts, dep, ret));
                }
            }

            // SORT: Cheapest first
            results.sort(Comparator.comparing(FlightDTO::getPrice));
            return results;

        } catch (Exception e) {
            System.err.println("Amadeus Error: " + e.getMessage());
            this.token = null; // Reset token if it expired
            return Collections.singletonList(createMockFlight(from, to, dep, ret));
        }
    }

    private FlightDTO mapToDTO(Map offer, Map dicts, String dep, String ret) {
        Map itinerary = (Map) ((List) offer.get("itineraries")).get(0);
        Map segment = (Map) ((List) itinerary.get("segments")).get(0);
        String carrierCode = (String) segment.get("carrierCode");
        String airlineName = ((Map) dicts.get("carriers")).getOrDefault(carrierCode, carrierCode).toString();

        BigDecimal price = new BigDecimal((String) ((Map) offer.get("price")).get("total"));
        int seats = (int) offer.get("numberOfBookableSeats");

        return new FlightDTO(
                UUID.randomUUID(),
                airlineName,
                carrierCode + segment.get("number"),
                (String) ((Map) segment.get("departure")).get("iataCode"),
                (String) ((Map) segment.get("arrival")).get("iataCode"),
                price,
                dep,
                ret != null && !ret.isEmpty() ? ret : "One-Way",
                "https://www.google.com/travel/flights?q=Flights%20to%20" + (String) ((Map) segment.get("arrival")).get("iataCode"),
                "Amadeus Real-Time",
                seats < 3 ? "ONLY " + seats + " LEFT" : "AVAILABLE"
        );
    }

    private FlightDTO createMockFlight(String from, String to, String dep, String ret) {
        return new FlightDTO(UUID.randomUUID(), "Sunny Wings", "SW101", from, to, new BigDecimal("299.00"), dep, ret, "https://google.com", "Fallback", "PROMOTION");
    }
}