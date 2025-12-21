package com.happiegolucky.backend.repository;

import com.happiegolucky.backend.entity.BookingEntity;
import com.happiegolucky.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
    // Find all bookings for a specific user
    List<BookingEntity> findByUser(UserEntity user);
}