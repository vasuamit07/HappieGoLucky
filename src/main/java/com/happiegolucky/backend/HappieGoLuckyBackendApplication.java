package com.happiegolucky.backend;

import com.happiegolucky.backend.entity.FlightEntity;
import com.happiegolucky.backend.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
public class HappieGoLuckyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappieGoLuckyBackendApplication.class, args);
    }

    // This @Bean runs when the application starts up
    @Bean
    public CommandLineRunner demoData(FlightRepository repository) {
        return (args) -> {
            System.out.println("--- Seeding Database with Flight Data ---");

            // Flight 1: JFK to LHR
            FlightEntity flight1 = new FlightEntity();
            flight1.setAirline("British Airways");
            flight1.setFlightNumber("BA234");
            flight1.setOrigin("JFK");
            flight1.setDestination("LHR");
            flight1.setPrice(BigDecimal.valueOf(550.99));
            flight1.setDepartureTime(LocalDateTime.now().plusDays(10).withHour(8).withMinute(0));
            flight1.setArrivalTime(LocalDateTime.now().plusDays(10).withHour(18).withMinute(0));
            repository.save(flight1);

            // Flight 2: JFK to LHR
            FlightEntity flight2 = new FlightEntity();
            flight2.setAirline("Happie Air");
            flight2.setFlightNumber("HG007");
            flight2.setOrigin("JFK");
            flight2.setDestination("LHR");
            flight2.setPrice(BigDecimal.valueOf(450.00));
            flight2.setDepartureTime(LocalDateTime.now().plusDays(10).withHour(12).withMinute(0));
            flight2.setArrivalTime(LocalDateTime.now().plusDays(10).withHour(22).withMinute(0));
            repository.save(flight2);

            // Flight 3: A different route
            FlightEntity flight3 = new FlightEntity();
            flight3.setAirline("Delta");
            flight3.setFlightNumber("DL99");
            flight3.setOrigin("JFK");
            flight3.setDestination("LAX");
            flight3.setPrice(BigDecimal.valueOf(250.00));
            flight3.setDepartureTime(LocalDateTime.now().plusDays(5).withHour(10).withMinute(0));
            flight3.setArrivalTime(LocalDateTime.now().plusDays(5).withHour(13).withMinute(0));
            repository.save(flight3);


            System.out.println("--- Seeding Complete ---");
        };
    }
}