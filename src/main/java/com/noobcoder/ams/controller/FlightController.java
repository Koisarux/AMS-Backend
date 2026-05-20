package com.noobcoder.ams.controller;

import com.noobcoder.ams.model.Flight;
import com.noobcoder.ams.model.FlightBookingSummary;
import com.noobcoder.ams.repository.FlightBookingSummaryRepository;
import com.noobcoder.ams.service.FlightService;
import com.noobcoder.ams.dto.FlightWithStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private FlightBookingSummaryRepository summaryRepository;

    @GetMapping("/admin/flights/summary")
    public ResponseEntity<List<FlightBookingSummary>> getFlightSummary() {
        return ResponseEntity.ok(summaryRepository.findAll());
    }

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
    public ResponseEntity<Page<Flight>> getAllFlights(Pageable pageable) {
        return ResponseEntity.ok(flightService.getAllFlights(pageable));
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
    public ResponseEntity<Page<Flight>> getAllFlightsPublic(Pageable pageable) {
        return ResponseEntity.ok(flightService.getAllFlights(pageable));
    }

    @GetMapping("/flights/status")
    public ResponseEntity<List<FlightWithStatus>> getFlightsWithStatus() {
        return ResponseEntity.ok(flightService.getFlightsWithStatus());
    }

    @GetMapping("/flights/search")
    public ResponseEntity<Page<Flight>> searchFlights(
            @RequestParam(value = "origin", required = false) String origin,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "date", required = false) String dateStr,
            Pageable pageable) {
        try {
            Page<Flight> flightsPage = flightService.getAllFlights(pageable);
            List<Flight> flights = flightsPage.getContent();

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

            // Note: Since this is in-memory filtering over paginated data, it might not return consistent pages.
            // In a real application, filtering should happen at the DB layer via FlightRepository.
            // We will return a sublist for now as a workaround since pagination applies to findAll.

            // Convert back to a new PageImpl? For simplicity, since the return type is Page<Flight>, we can just return a PageImpl.
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), flights.size());
            List<Flight> pagedFlights = flights.subList(Math.min(start, flights.size()), end);
            
            return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(pagedFlights, pageable, flights.size()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error processing search request: " + e.getMessage());
        }
    }
}