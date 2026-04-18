package com.logistic.platform.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.platform.models.Driver;
import com.logistic.platform.models.Location;
import com.logistic.platform.repository.DriverRepository;
import com.logistic.platform.services.KafkaService;

@RestController
@RequestMapping("/logistics/location")
public class LocationController {

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private KafkaTemplate<String, Location> kafkaTemplate;

    @Autowired
    private DriverRepository driverRepository;

    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(@RequestParam String driverId,
                                                 @RequestParam double latitude,
                                                 @RequestParam double longitude) {
        Location location = new Location(driverId, latitude, longitude, System.currentTimeMillis());
        kafkaTemplate.send("driver-locations", driverId , location);
        driverRepository.findById(Integer.parseInt(driverId)).ifPresent(driver -> {
            driver.setDriverLat(latitude);
            driver.setDriverLon(longitude);
            driverRepository.save(driver);
        });
        System.out.println(kafkaService.getDriverLocation(driverId));
        return ResponseEntity.ok("Location update received");
    }
}
