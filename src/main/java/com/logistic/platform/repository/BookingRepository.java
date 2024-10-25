package com.logistic.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistic.platform.models.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    long countByStatus(String status);
    List<Booking> findByStatus(String status);
    List<Booking> findByDriverId(int driverId);
}
