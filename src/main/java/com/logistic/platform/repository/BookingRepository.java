package com.logistic.platform.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // long countByStatus(String status);
    // List<Booking> findByStatus(String status);
    List<Booking> findByDriverId(int driverId);
    List<Booking> findByUserId(int userId);
    
    @Query(value="select count(*) from Bookings where driver_id = :id",nativeQuery=true)
    int countByDriver(int id);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByCreatedAtBetweenAndVehicleTypeIgnoreCase(LocalDateTime start, LocalDateTime end, String vehicleType);

    List<Booking> findByCreatedAtBetweenAndVehicleTypeIgnoreCase(LocalDateTime start, LocalDateTime end, String vehicleType);

    long countByStatusIn(List<BookingStatus> statuses);

    @Query("""
            select count(b) from Booking b
            where function('hour', b.createdAt) = :hourOfDay
              and b.createdAt >= :since
            """)
    long countByHourOfDaySince(@Param("hourOfDay") int hourOfDay, @Param("since") LocalDateTime since);

    @Query("""
            select count(b) from Booking b
            where function('dayofweek', b.createdAt) = function('dayofweek', :referenceDate)
              and function('hour', b.createdAt) = :hourOfDay
              and b.createdAt >= :since
            """)
    long countBySameWeekdayAndHourSince(
            @Param("referenceDate") LocalDateTime referenceDate,
            @Param("hourOfDay") int hourOfDay,
            @Param("since") LocalDateTime since);

}
