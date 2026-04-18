package com.logistic.platform.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.models.Location;
import com.logistic.platform.services.BookingService;
import com.logistic.platform.services.DriverService;
import com.logistic.platform.services.KafkaService;

@Controller
public class DriverLocationController {

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private BookingService bookingService;

     @GetMapping("/driver-map/{driverId}")
    public String showDriverMap(@PathVariable int driverId, Model model) {
        Driver driver = driverService.getDriver(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        Booking activeBooking = bookingService.getDriverDetails(driverId).stream()
                .filter(booking -> booking.getStatus() != null)
                .filter(booking -> booking.getStatus().name().equals("UNDER_PROCESS")
                        || booking.getStatus().name().equals("PENDING"))
                .findFirst()
                .orElse(null);

        Location dropOffLocation = new Location();
        dropOffLocation.setDriverId(String.valueOf(driverId));
        dropOffLocation.setLatitude(activeBooking != null ? activeBooking.getDropoffLat() : driver.getDriverLat());
        dropOffLocation.setLongitude(activeBooking != null ? activeBooking.getDropoffLon() : driver.getDriverLon());
        dropOffLocation.setTimestamp(System.currentTimeMillis());

        model.addAttribute("dropOffLocation", dropOffLocation);
        model.addAttribute("driverId", String.valueOf(driverId));
        return "driver-location-map"; 
    }

      @GetMapping("api/driver-location/{driverId}")
    public ResponseEntity<Location> getDriverLocation(@PathVariable String driverId) {
        Location location = kafkaService.getDriverLocation(driverId);
        if (location != null) {
            return ResponseEntity.ok(location);
        }
        return driverService.getDriver(Integer.parseInt(driverId))
                .map(driver -> new Location(driverId, driver.getDriverLat(), driver.getDriverLon(), System.currentTimeMillis()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
