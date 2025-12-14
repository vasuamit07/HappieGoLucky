package com.happiegolucky.backend.controller;

import com.happiegolucky.backend.api.controller.SearchApi;
import com.happiegolucky.backend.api.model.Flight;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal; // <--- Make sure this is here
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class FlightController implements SearchApi {

    @Override
    public ResponseEntity<List<Flight>> searchFlights(String from, String to, LocalDate date) {

        Flight dummyFlight = new Flight();
        dummyFlight.setId(UUID.randomUUID());
        dummyFlight.setAirline("Happie Air");
        dummyFlight.setFlightNumber("HG101");

        // This line caused the error before.
        // Now that api.yaml uses 'decimal', this will work perfectly.
        dummyFlight.setPrice(BigDecimal.valueOf(150.00));

        dummyFlight.setDepartureTime(OffsetDateTime.now());
        dummyFlight.setArrivalTime(OffsetDateTime.now().plusHours(2));

        List<Flight> flights = new ArrayList<>();
        flights.add(dummyFlight);

        return ResponseEntity.ok(flights);
    }
}
