package com.noobcoder.ams.service;

import com.noobcoder.ams.model.Flight;
import com.noobcoder.ams.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.noobcoder.ams.dto.FlightWithStatus;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public Flight addFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public Page<Flight> getAllFlights(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by("flightNumber").ascending()
            );
        }
        return flightRepository.findAll(pageable);
    }

    public Page<FlightWithStatus> getFlightsWithStatus(Pageable pageable) {
        return flightRepository.findAllFlightsWithStatus(pageable);
    }

    public Optional<Flight> getFlightByNumber(String flightNumber) {
        return flightRepository.findById(flightNumber);
    }

    public Flight updateFlight(String flightNumber, Flight updatedFlight) {
        updatedFlight.setFlightNumber(flightNumber);
        return flightRepository.save(updatedFlight);
    }

    public void deleteFlight(String flightNumber) {
        flightRepository.deleteById(flightNumber);
    }
}