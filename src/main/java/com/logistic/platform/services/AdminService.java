package com.logistic.platform.services;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.repository.BookingRepository;
import com.logistic.platform.repository.DriverRepository;

@Service
public class AdminService {

     @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Fleet Management
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Booking> getAllBooking()
    {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getSingleBooking(int id)
    {
         return bookingRepository.findById(id);
        
    }

    public Optional<Driver> getSingleDriver(int id)
    {
         return driverRepository.findById(id);
        
    }

    public Driver updateDriverStatus(int driverId, String status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setStatus(status);
        return driverRepository.save(driver);
    }

    public int number_of_booking_driver(int id)
    {
        return bookingRepository.countByDriver(id);
    }

    // Data Analytics
    public long getTotalTripsCompleted() {
        return bookingRepository.countByStatus("COMPLETED");
    }

    public double getAverageTripTime() {
        List<Booking> completedBookings = bookingRepository.findByStatus("COMPLETED");
        return completedBookings.stream()
                .mapToLong(booking -> Duration.between(booking.getCreatedAt(), booking.getDeliverAt()).toMinutes())
                .average()
                .orElse(0);
    }

    public Map<Object, Long> getDriverPerformance() {
        List<Booking> completedBookings = bookingRepository.findByStatus("COMPLETED");
        return completedBookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getDriver().getId(),
                        Collectors.counting()
                ));
    }
}
