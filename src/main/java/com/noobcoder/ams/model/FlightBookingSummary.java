package com.noobcoder.ams.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "flight_booking_summary")
public class FlightBookingSummary {
    @Id
    private String flightNumber;
    private String origin;
    private String destination;
    private Integer totalSeats;
    private Integer availableSeats;
    private Integer bookedSeats;

    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public Integer getTotalSeats() { return totalSeats; }
    public Integer getAvailableSeats() { return availableSeats; }
    public Integer getBookedSeats() { return bookedSeats; }
}
