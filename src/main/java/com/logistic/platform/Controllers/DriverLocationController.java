package com.logistic.platform.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.logistic.platform.models.Location;
import com.logistic.platform.services.BookingService;
import com.logistic.platform.services.KafkaService;

@Controller
public class DriverLocationController {

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private BookingService bookingService;

     @GetMapping("/driver-map/{driverId}")
    public String showDriverMap(@PathVariable int driverId, Model model) {
        String str=String.valueOf(driverId);
        // Booking latestBooking = driverService.getLatestBookingForDriver(driverId);
        // if (latestBooking != null) {
        //     model.addAttribute("dropOffLocation", latestBooking.getDropOffLocation());
        // }
        Location ll=new Location();
        ll.setDriverId("11");
        ll.setLatitude(48.8115);
        ll.setLongitude(2.3522);
        ll.setTimestamp(1730579);
        model.addAttribute("dropOffLocation",ll);
        model.addAttribute("driverId", str);
        return "driver-location-map"; 
    }

      @GetMapping("api/driver-location/{driverId}")
    public ResponseEntity<Location> getDriverLocation(@PathVariable String driverId) {
        Location location = kafkaService.getDriverLocation(driverId);
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
