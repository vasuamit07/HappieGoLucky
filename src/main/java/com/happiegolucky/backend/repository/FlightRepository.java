package com.happiegolucky.backend.repository;

import com.happiegolucky.backend.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, UUID> {

    // We can define custom queries just by naming methods correctly!
    // This finds flights going FROM one place TO another
    List<FlightEntity> findByOriginAndDestination(String origin, String destination);
}