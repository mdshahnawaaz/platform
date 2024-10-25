package com.logistic.platform.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.services.BookingService;
import com.logistic.platform.services.DriverService;

@Controller
@RequestMapping("/logistics/drivers")
public class DriverController {

    // @Autowired
    // private KafkaService kafkaService;

     @Autowired
    private DriverService driverService;


    @Autowired
    private BookingService bookingService;

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriver(@PathVariable int id) {
        return driverService.getDriver(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/accept-booking")
    public ResponseEntity<String> acceptBooking(@PathVariable int id, @RequestBody Booking booking) {
        boolean accepted = driverService.acceptBooking(id, booking);
        if (accepted) {
            return ResponseEntity.ok("Booking accepted");
        } else {
            return ResponseEntity.badRequest().body("Unable to accept booking");
        }
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<String> updateJobStatus(@PathVariable int id, @RequestParam String status) {
        boolean updated = driverService.updateJobStatus(id, status);
        if (updated) {
            return ResponseEntity.ok("Status updated");
        } else {
            return ResponseEntity.badRequest().body("Unable to update status");
        }
    }

    @GetMapping("/{id}/allbooking")
    public String getMethodName(@PathVariable int id,Model model) {
        List<Booking>booking= bookingService.getDriverDetails(id);
        model.addAttribute("bookings",booking);
        return "driver_view";
    }
    
    // @PostMapping("/update")
    // public ResponseEntity<?> updateLocation(@RequestParam int id,@RequestParam double driverlat,@RequestParam double driverlon)
    // {
    //     List<Double>loc=new ArrayList<>();
    //     loc.add(driverlat);
    //     loc.add(driverlon);
        
    //     this.kafkaService.updateLocation(loc);
    //     return new ResponseEntity<>(Map.of("message","Location updated"),HttpStatus.OK);
    // }

}
