package com.happiegolucky.backend.service;

import com.happiegolucky.backend.entity.BookingEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendBookingConfirmation(BookingEntity booking) {
        // In a real app, we would use JavaMailSender here.
        // For now, we "Doodle" the email to the system console!
        System.out.println("---------------------------------------------------");
        System.out.println("📧 DOODLE EMAIL SENT TO: " + booking.getUser().getEmail());
        System.out.println("Subject: Pack your bags! Booking Confirmed");
        System.out.println("Message: Hi " + booking.getUser().getUsername() + ",");
        System.out.println("Your flight " + booking.getFlight().getFlightNumber() + " to "
                + booking.getFlight().getDestination() + " is booked!");
        System.out.println("---------------------------------------------------");
    }
}