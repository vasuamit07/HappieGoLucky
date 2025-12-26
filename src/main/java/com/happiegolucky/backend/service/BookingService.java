package com.happiegolucky.backend.service;

import com.happiegolucky.backend.entity.BookingEntity;
import com.happiegolucky.backend.entity.FlightEntity;
import com.happiegolucky.backend.entity.UserEntity;
import com.happiegolucky.backend.repository.BookingRepository;
import com.happiegolucky.backend.repository.FlightRepository;
import com.happiegolucky.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Creates a new booking for the currently logged-in user.
     */
    public BookingEntity bookFlight(UUID flightId) {
        // 1. Identify the logged-in user from the Security Context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 2. Find the flight being booked
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with ID: " + flightId));

        // 3. Create the Booking record
        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        // 4. Save to database
        BookingEntity savedBooking = bookingRepository.save(booking);

        // 5. Trigger the simulated notification (console doodle)
        notificationService.sendBookingConfirmation(savedBooking);

        return savedBooking;
    }

    /**
     * Retrieves all bookings belonging to the currently logged-in user.
     */
    public List<BookingEntity> getUserBookings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUser(user);
    }
}