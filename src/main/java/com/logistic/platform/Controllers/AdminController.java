package com.logistic.platform.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.platform.models.Driver;
import com.logistic.platform.services.AdminService;

@RestController
@RequestMapping("/logistics/admin")
public class AdminController {
     @Autowired
    private AdminService adminService;

    // Fleet Management
    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(adminService.getAllDrivers());
    }

    @PutMapping("/drivers/{id}/status")
    public ResponseEntity<Driver> updateDriverStatus(@PathVariable int id, @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateDriverStatus(id, status));
    }

    // Data Analytics
    @GetMapping("/analytics/trips-completed")
    public ResponseEntity<Long> getTotalTripsCompleted() {
        return ResponseEntity.ok(adminService.getTotalTripsCompleted());
    }

    @GetMapping("/analytics/average-trip-time")
    public ResponseEntity<Double> getAverageTripTime() {
        return ResponseEntity.ok(adminService.getAverageTripTime());
    }

    @GetMapping("/analytics/driver-performance")
    public ResponseEntity<Map<Object, Long>> getDriverPerformance() {
        return ResponseEntity.ok(adminService.getDriverPerformance());
    }
}
