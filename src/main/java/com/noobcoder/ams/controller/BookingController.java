package com.noobcoder.ams.controller;

import com.noobcoder.ams.model.Booking;
import com.noobcoder.ams.model.User;
import com.noobcoder.ams.repository.UserRepository;
import com.noobcoder.ams.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/book")
    public ResponseEntity<Booking> bookFlight(
            @RequestParam String flightNumber,
            @RequestParam int numberOfTickets,
            @RequestParam(required = false) String passengerName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String passportNumber,
            @RequestParam(required = false) String paymentMethod) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Long userId = user.getId();
        if (numberOfTickets <= 0) {
            throw new IllegalArgumentException("Number of tickets must be greater than zero");
        }
        Booking booking = bookingService.bookFlight(userId, flightNumber, numberOfTickets, 
                passengerName, gender, passportNumber, paymentMethod);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getUserBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(bookingService.getUserBookingsByEmail(email));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Booking test endpoint works!");
    }
}