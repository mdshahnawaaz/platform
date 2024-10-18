package com.logistic.platform.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.services.DriverService;

@RestController
@RequestMapping("/logistics/drivers")
public class DriverController {

    // @Autowired
    // private KafkaService kafkaService;

     @Autowired
    private DriverService driverService;


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
