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

    public BookingEntity bookFlight(UUID flightId) {
        // 1. Get the username of the person currently logged in
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        // 2. Create the booking
        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingDate(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public List<BookingEntity> getUserBookings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        return bookingRepository.findByUser(user);
    }
}