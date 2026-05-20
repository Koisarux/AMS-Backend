package com.noobcoder.ams.repository;

import com.noobcoder.ams.model.FlightBookingSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightBookingSummaryRepository extends JpaRepository<FlightBookingSummary, String> {
}
