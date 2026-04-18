package com.logistic.platform.Controllers;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.logistic.platform.models.AdminDashboardData;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.services.AdminService;

@Controller
@RequestMapping("/logistics/admin")
public class AdminController {
    private static final String ADMIN_SESSION_KEY = "adminAuthenticated";

     @Autowired
    private AdminService adminService;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY))) {
            return "redirect:/logistics/admin/dashboard";
        }
        return "admin_login";
    }

    @PostMapping("/login")
    public String loginAdmin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            session.setAttribute(ADMIN_SESSION_KEY, true);
            return "redirect:/logistics/admin/dashboard";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Invalid admin username or password.");
        return "redirect:/logistics/admin/login";
    }

    @PostMapping("/logout")
    public String logoutAdmin(HttpSession session) {
        session.removeAttribute(ADMIN_SESSION_KEY);
        return "redirect:/logistics/admin/login";
    }

    @GetMapping("/dashboard")
    public String getDashboard(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/logistics/admin/login";
        }
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
    public String getAllBooking(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/logistics/admin/login";
        }
        List<Booking>booking=adminService.getAllBooking();
        model.addAttribute("bookings", booking);
        List<Driver>drivers=adminService.getAllDrivers();
        model.addAttribute("drivers",drivers);
        return "admin_view";

    }
    @GetMapping("/bookings/{id}")
    public String getSingleBooking(@PathVariable("id") int id, HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/logistics/admin/login";
        }
        Optional<Booking> booking=adminService.getSingleBooking(id);
        model.addAttribute("booking", booking);
        return "admin_user";

    }
    
    @GetMapping("/drivers/{id}")
    public String getSingleDriver(@PathVariable("id") int id, HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/logistics/admin/login";
        }
        Optional<Driver> driver=adminService.getSingleDriver(id);
        model.addAttribute("driver", driver);
        int count=adminService.number_of_booking_driver(id);
        model.addAttribute("count_of_booking",count);
        return "admin_driver";

    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers(HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(adminService.getAllDrivers());
    }

    

    @PutMapping("/drivers/{id}/status")
    public ResponseEntity<Driver> updateDriverStatus(@PathVariable int id, @RequestParam String status, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(adminService.updateDriverStatus(id, status));
    }

    private boolean isAdminAuthenticated(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY));
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
