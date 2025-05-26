package com.noobcoder.ams.controller;

import com.noobcoder.ams.model.Flight;
import com.noobcoder.ams.service.FlightService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @PostMapping("/admin/flights")
    public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {
        if (flight.getTotalSeats() < 0 || flight.getAvailableSeats() < 0) {
            throw new IllegalArgumentException("Total seats and available seats must be non-negative");
        }
        if (flight.getAvailableSeats() > flight.getTotalSeats()) {
            throw new IllegalArgumentException("Available seats cannot exceed total seats");
        }
        Flight savedFlight = flightService.addFlight(flight);
        return ResponseEntity.ok(savedFlight);
    }

    @GetMapping("/admin/flights")
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/admin/flights/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(@PathVariable String flightNumber) {
        Flight flight = flightService.getFlightByNumber(flightNumber)
                .orElseThrow(() -> new EntityNotFoundException("Flight with number " + flightNumber + " not found"));
        return ResponseEntity.ok(flight);
    }

    @PutMapping("/admin/flights/{flightNumber}")
    public ResponseEntity<Flight> updateFlight(@PathVariable String flightNumber, @RequestBody Flight flight) {
        Flight updatedFlight = flightService.updateFlight(flightNumber, flight);
        return ResponseEntity.ok(updatedFlight);
    }

    @DeleteMapping("/admin/flights/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        flightService.deleteFlight(flightNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/flights")
    public ResponseEntity<List<Flight>> getAllFlightsPublic() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/flights/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam(value = "origin", required = false) String origin,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "date", required = false) String dateStr) {
        try {
            List<Flight> flights = flightService.getAllFlights();

            // Filter by origin if provided
            if (origin != null && !origin.trim().isEmpty()) {
                String finalOrigin = origin.trim();
                flights = flights.stream()
                        .filter(flight -> flight.getOrigin().equalsIgnoreCase(finalOrigin))
                        .collect(Collectors.toList());
            }

            // Filter by destination if provided
            if (destination != null && !destination.trim().isEmpty()) {
                String finalDestination = destination.trim();
                flights = flights.stream()
                        .filter(flight -> flight.getDestination().equalsIgnoreCase(finalDestination))
                        .collect(Collectors.toList());
            }

            // Filter by date if provided
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate searchDate;
                try {
                    searchDate = LocalDate.parse(dateStr.trim(), formatter);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid date format: " + dateStr + ". Expected format: yyyy-MM-dd");
                }
                flights = flights.stream()
                        .filter(flight -> flight.getDepartureTime().toLocalDate().isEqual(searchDate))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error processing search request: " + e.getMessage());
        }
    }
}