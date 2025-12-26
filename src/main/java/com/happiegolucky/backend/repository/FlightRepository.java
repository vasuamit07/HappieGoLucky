package com.happiegolucky.backend.repository;

import com.happiegolucky.backend.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, UUID> {
    List<FlightEntity> findByOriginAndDestination(String origin, String destination);

    // NEW: Find specific flight by code
    Optional<FlightEntity> findByFlightNumberIgnoreCase(String flightNumber);
}