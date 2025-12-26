package com.happiegolucky.backend.controller;

import com.happiegolucky.backend.entity.BookingEntity;
import com.happiegolucky.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * POST /api/bookings/{flightId}
     * Saves a flight to the user's account.
     */
    @PostMapping("/{flightId}")
    public ResponseEntity<String> createBooking(@PathVariable UUID flightId) {
        try {
            bookingService.bookFlight(flightId);
            return ResponseEntity.ok("Success! Flight Doodled into your bookings.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating booking: " + e.getMessage());
        }
    }

    /**
     * GET /api/bookings/my
     * Returns a JSON list of all flights booked by the current user.
     */
    @GetMapping("/my")
    public ResponseEntity<List<BookingEntity>> getMyBookings() {
        try {
            List<BookingEntity> myBookings = bookingService.getUserBookings();
            return ResponseEntity.ok(myBookings);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}