package com.logistic.platform.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.logistic.platform.Configuration.PortalAuthenticationService;
import com.logistic.platform.models.AdminDashboardData;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.services.AdminService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/logistics/admin")
public class AdminController {
     @Autowired
    private AdminService adminService;

    @Autowired
    private PortalAuthenticationService portalAuthenticationService;

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream().anyMatch(item -> "ROLE_ADMIN".equals(item.getAuthority()))) {
            return "redirect:/logistics/admin/dashboard";
        }
        return "admin_login";
    }

    @PostMapping("/login")
    public String loginAdmin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        try {
            portalAuthenticationService.authenticate("admin", username, password, request, response);
            return "redirect:/logistics/admin/dashboard";
        } catch (AuthenticationException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/logistics/admin/login";
        }
    }

    @PostMapping("/logout")
    public String logoutAdmin(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        portalAuthenticationService.logout(request, response, authentication);
        return "redirect:/logistics/admin/login";
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        AdminDashboardData dashboardData = adminService.getDashboardData();
        model.addAttribute("dashboard", dashboardData);
        model.addAttribute("completedTrips", dashboardData.completedTrips());
        model.addAttribute("pendingBookings", dashboardData.pendingBookings());
        model.addAttribute("freeDrivers", dashboardData.availableDrivers());
        model.addAttribute("bookedDrivers", dashboardData.busyDrivers());
        model.addAttribute("tripStatus", dashboardData.bookingStatusBreakdown());
        model.addAttribute("revenueData", dashboardData.weeklyRevenue());

        return "admin_dashboard";
    }
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
    public String getSingleBooking(@PathVariable("id") int id, Model model) {
        Optional<Booking> booking=adminService.getSingleBooking(id);
        model.addAttribute("booking", booking);
        return "admin_user";

    }
    
    @GetMapping("/drivers/{id}")
    public String getSingleDriver(@PathVariable("id") int id, Model model) {
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
    // @GetMapping("/analytics/trips-completed")
    // public ResponseEntity<Long> getTotalTripsCompleted() {
    //     return ResponseEntity.ok(adminService.getTotalTripsCompleted());
    // }

    // @GetMapping("/analytics/average-trip-time")
    // public ResponseEntity<Double> getAverageTripTime() {
    //     return ResponseEntity.ok(adminService.getAverageTripTime());
    // }

    // @GetMapping("/analytics/driver-performance")
    // public ResponseEntity<Map<Object, Long>> getDriverPerformance() {
    //     return ResponseEntity.ok(adminService.getDriverPerformance());
    // }
}
