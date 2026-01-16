package com.happiegolucky.backend.service;

import com.happiegolucky.backend.dto.FlightDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.*;

//@Service
//@RequiredArgsConstructor
//public class FlightAggregatorService {
//
//    private final RestTemplate restTemplate; // Requires a Bean in SecurityConfig or a separate Config class
//
//    @Value("${api.aviationstack.key}")
//    private String apiKey;
//
//    public List<FlightDTO> getAggregatedFlights(String from, String to, String depDate, String retDate) {
//        List<FlightDTO> results = new ArrayList<>();
//
//        // Base URL for AviationStack
//        String url = String.format("http://api.aviationstack.com/v1/flights?access_key=%s&dep_iata=%s&arr_iata=%s",
//                apiKey, from.toUpperCase(), to.toUpperCase());
//
//        try {
//            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
//
//            if (response != null && response.get("data") != null) {
//                List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
//
//                for (Map<String, Object> item : data) {
//                    results.add(mapToDTO(item, from, to, depDate, retDate));
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Aggregator Error: " + e.getMessage());
//        }
//
//        // FALLBACK: If API is empty or fails, add one "Mock" deal so the user sees something
//        if (results.isEmpty()) {
//            results.add(createMockFlight(from, to, depDate, retDate));
//        }
//
//        return results;
//    }
//
//    private FlightDTO mapToDTO(Map<String, Object> item, String from, String to, String depDate, String retDate) {
//        FlightDTO f = new FlightDTO();
//        f.setId(UUID.randomUUID());
//
//        // Safe extraction with Null Checks
//        Map<String, Object> airlineMap = (Map<String, Object>) item.get("airline");
//        Map<String, Object> flightMap = (Map<String, Object>) item.get("flight");
//
//        f.setAirline(airlineMap != null ? (String) airlineMap.get("name") : "Global Airways");
//        f.setFlightNumber(flightMap != null ? (String) flightMap.get("iata") : "GL101");
//
//        f.setOrigin(from.toUpperCase());
//        f.setDestination(to.toUpperCase());
//        f.setDepartureDate(depDate);
//        f.setReturnDate(retDate);
//
//        // Calculate a realistic price: Base $200 + random variation
//        // $Price = 200 + (random \times 500)$
//        f.setPrice(new BigDecimal(200 + Math.random() * 500));
//
//        f.setProvider("AviationStack Real-Time");
//        f.setStatus("CONFIRMED");
//        return f;
//    }
//
//    private FlightDTO createMockFlight(String from, String to, String depDate, String retDate) {
//        return new FlightDTO(
//                UUID.randomUUID(),
//                "Sunny Wings (Special Deal)",
//                "SW-" + (int)(Math.random() * 900 + 100),
//                from.toUpperCase(),
//                to.toUpperCase(),
//                new BigDecimal("249.99"),
//                depDate,
//                retDate != null ? retDate : "One-Way",
//                "https://www.google.com/travel/flights", // Added this: bookingUrl
//                "HappieGoLucky Exclusive",
//                "PROMOTION"
//        );
//    }
//}
@Service
public class FlightAggregatorService {

    @Autowired
    private AmadeusService amadeusService;

    @Autowired
    private AirportService airportService;

    public List<FlightDTO> getAllFlights(String from, String to, String dep, String ret, int adults) {
        // 1. Get real bookable deals from Amadeus (Actual real-time prices)
        List<FlightDTO> realFlights = amadeusService.getRealFlights(from, to, dep, ret, adults);

        // 2. Enhance the data with links from your airports.json
        for (FlightDTO flight : realFlights) {
            // This pulls the "Information" URL from your local JSON file
            String destinationInfo = airportService.getAirportInfo(flight.getDestination());
            flight.setBookingUrl(destinationInfo);

            // Set provider name for the UI
            flight.setProvider("Amadeus GDS (Live)");
        }

        return realFlights;
    }
}