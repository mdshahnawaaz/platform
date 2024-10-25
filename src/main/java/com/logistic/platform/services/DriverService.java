package com.logistic.platform.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.repository.DriverRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DriverService {

     @Autowired
    private DriverRepository driverRepository;

    public Optional<Driver> getDriver(int id) {
        return driverRepository.findById(id);
    }

    public boolean acceptBooking(int driverId, Booking booking) {
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            if (driver.getCurrentJob() == null) {
                driver.setCurrentJob(booking);
                driver.setStatus("Accepted");
                driver.setAvailable(false);
                System.out.println("within acceptedBooking drivers" + driver);
                // driver.setId(6);
                driverRepository.save(driver);
                System.out.println("accepteded Booking " );
                return true;
            }
        }
        return false;
    }

    public boolean updateJobStatus(int driverId, String status) {
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            if (driver.getCurrentJob() != null) {
                // driver.setStatus(status);

                if ("Delivered".equals(status)) {
                    System.out.println("status changed");
                    driver.setCurrentJob(null);
                    driver.setStatus("Available");
                    driver.setAvailable(true);
                }
                driverRepository.save(driver);
                return true;
            }
        }
        return false;
    }

    public List<Driver> getActiveBookingDrivers()
    {
        return driverRepository.findByStatus("Accepted");
    }

    
}
