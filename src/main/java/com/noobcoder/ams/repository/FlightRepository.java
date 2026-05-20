package com.noobcoder.ams.repository;

import com.noobcoder.ams.model.Flight;
import com.noobcoder.ams.dto.FlightWithStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, String> {
    Optional<Flight> findByFlightNumber(String flightNumber);

    @Query(value = "SELECT f.flight_number as flightNumber, f.departure_time as departureTime, " +
                   "f.arrival_time as arrivalTime, f.origin as origin, f.destination as destination, " +
                   "f.total_seats as totalSeats, f.available_seats as availableSeats, f.price as price, " +
                   "CASE WHEN f.available_seats = 0 THEN 'SOLD OUT' " +
                   "     WHEN f.available_seats < 10 THEN 'FILLING FAST' " +
                   "     ELSE 'AVAILABLE' END as bookingStatus " +
                   "FROM flights f ORDER BY f.flight_number ASC",
           countQuery = "SELECT COUNT(*) FROM flights",
           nativeQuery = true)
    Page<FlightWithStatus> findAllFlightsWithStatus(Pageable pageable);
}