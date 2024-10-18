package com.logistic.platform.Controllers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.logistic.platform.models.Location;
import com.logistic.platform.services.LocationService;

@RestController
public class TrackingController {

    @Autowired
    private LocationService locationService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @GetMapping("/track/{driverId}")
    public SseEmitter streamDriverLocation(@PathVariable String driverId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        executorService.execute(() -> {
            try {
                while (true) {
                    Location location = locationService.getDriverLocation(driverId);
                    if (location != null) {
                        emitter.send(SseEmitter.event().data(location));
                    }
                    Thread.sleep(5000); // Wait for 5 seconds before next update
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
}
