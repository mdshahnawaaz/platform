package com.logistic.platform.Controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.services.AdminService;

@Controller
@RequestMapping("/logistics/admin")
public class AdminController {
     @Autowired
    private AdminService adminService;

    // Fleet Management
    @GetMapping("/bookings")
    public String getAllBooking(Model model) {
        List<Booking>booking=adminService.getAllBooking();
        model.addAttribute("bookings", booking);
        List<Driver>drivers=adminService.getAllDrivers();
        model.addAttribute("drivers",drivers);
        return "admin_view";

    }
    @GetMapping("/bookings/{id}")
    public String getSingleBooking(@PathVariable("id") int id,Model model) {
        Optional<Booking> booking=adminService.getSingleBooking(id);
        model.addAttribute("booking", booking);
        return "admin_user";

    }
    
    @GetMapping("/drivers/{id}")
    public String getSingleDriver(@PathVariable("id") int id,Model model) {
        Optional<Driver> driver=adminService.getSingleDriver(id);
        model.addAttribute("driver", driver);
        int count=adminService.number_of_booking_driver(id);
        model.addAttribute("count_of_booking",count);
        return "admin_driver";

    }

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
