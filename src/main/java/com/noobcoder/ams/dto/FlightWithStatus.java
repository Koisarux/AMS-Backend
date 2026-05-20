package com.noobcoder.ams.dto;

import java.time.LocalDateTime;

public interface FlightWithStatus {
    String getFlightNumber();
    LocalDateTime getDepartureTime();
    LocalDateTime getArrivalTime();
    String getOrigin();
    String getDestination();
    Integer getTotalSeats();
    Integer getAvailableSeats();
    Double getPrice();
    String getBookingStatus();
}
