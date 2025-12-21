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

    @PostMapping("/{flightId}")
    public ResponseEntity<String> createBooking(@PathVariable UUID flightId) {
        bookingService.bookFlight(flightId);
        return ResponseEntity.ok("Flight booked successfully!");
    }
    @GetMapping("/my")
    public ResponseEntity<List<BookingEntity>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getUserBookings());
    }
}