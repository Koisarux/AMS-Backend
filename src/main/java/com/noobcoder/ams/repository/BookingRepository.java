package com.noobcoder.ams.repository;

import com.noobcoder.ams.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    @Query(value = "SELECT b.* FROM bookings b JOIN users u ON b.user_id = u.id WHERE u.email = :email", nativeQuery = true)
    List<Booking> findBookingsByUserEmail(@Param("email") String email);

    @Procedure(procedureName = "cancel_booking")
    void cancelBookingProcedure(@Param("booking_id") Long bookingId);
}