package com.logistic.platform.Controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.BookingStatus;
import com.logistic.platform.models.Driver;
import com.logistic.platform.services.BookingService;
import com.logistic.platform.services.DriverService;

@Controller
@RequestMapping("/logistics/drivers")
public class DriverController {

    private static final String DRIVER_SESSION_KEY = "loggedInDriverId";

    // @Autowired
    // private KafkaService kafkaService;

     @Autowired
    private DriverService driverService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/portal")
    public String getDriverPortal(
            @RequestParam(required = false) Integer bookingId,
            HttpSession session,
            Model model) {
        Integer driverId = (Integer) session.getAttribute(DRIVER_SESSION_KEY);
        if (driverId == null) {
            return "driver_login";
        }

        Optional<Driver> driverOpt = driverService.getDriver(driverId);
        if (driverOpt.isEmpty()) {
            session.invalidate();
            model.addAttribute("errorMessage", "Driver session expired. Please log in again.");
            return "driver_login";
        }

        populateDriverDashboard(model, driverOpt.get(), bookingId);
        return "driver_portal";
    }

    @PostMapping("/login")
    public String loginDriver(
            @RequestParam int driverId,
            @RequestParam String licenseNumber,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        return driverService.authenticateDriver(driverId, licenseNumber)
                .map(driver -> {
                    session.setAttribute(DRIVER_SESSION_KEY, driver.getId());
                    return "redirect:/logistics/drivers/portal";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid driver ID or license number.");
                    return "redirect:/logistics/drivers/portal";
                });
    }

    @PostMapping("/logout")
    public String logoutDriver(HttpSession session) {
        session.invalidate();
        return "redirect:/logistics/drivers/portal";
    }

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

    @PostMapping("/{driverId}/bookings/{bookingId}/send-completion-code")
    public String sendCompletionCode(
            @PathVariable int driverId,
            @PathVariable int bookingId,
            RedirectAttributes redirectAttributes) {
        try {
            driverService.sendDeliveryCompletionCode(driverId, bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Verification code sent to the user email.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/logistics/drivers/" + driverId + "/allbooking";
    }

    @PostMapping("/{driverId}/bookings/{bookingId}/complete")
    public String completeBooking(
            @PathVariable int driverId,
            @PathVariable int bookingId,
            @RequestParam String verificationCode,
            RedirectAttributes redirectAttributes) {
        try {
            driverService.completeBookingWithVerification(driverId, bookingId, verificationCode);
            redirectAttributes.addFlashAttribute("successMessage", "Booking completed successfully. Driver is now available for the next booking.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/logistics/drivers/" + driverId + "/allbooking";
    }


    @GetMapping("/{id}/allbooking")
    public String getMethodName(@PathVariable int id,Model model) {
        Driver driver = driverService.getDriver(id).orElse(null);
        if (driver == null) {
            model.addAttribute("driver", null);
            return "driver_view";
        }
        populateDriverDashboard(model, driver, null);
        return "driver_view";
    }

    private void populateDriverDashboard(Model model, Driver driver, Integer selectedBookingId) {
        List<Booking> bookings = bookingService.getDriverDetails(driver.getId());
        List<Booking> activeBookingList = bookings.stream()
                .filter(item -> item.getStatus() == BookingStatus.PENDING || item.getStatus() == BookingStatus.UNDER_PROCESS)
                .toList();
        List<Booking> bookingHistory = bookings.stream()
                .filter(item -> item.getStatus() == BookingStatus.DELIVERED)
                .sorted((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()))
                .toList();

        Booking selectedBooking = resolveSelectedBooking(activeBookingList, selectedBookingId);

        long activeBookings = activeBookingList.size();
        long completedBookings = bookingHistory.size();
        BigDecimal totalEarnings = bookings.stream()
                .filter(item -> item.getEstimatedCost() != null)
                .map(Booking::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("driver", driver);
        model.addAttribute("bookings", bookings);
        model.addAttribute("activeBookingList", activeBookingList);
        model.addAttribute("bookingHistory", bookingHistory);
        model.addAttribute("selectedBooking", selectedBooking);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("totalEarnings", totalEarnings);
        model.addAttribute("driverId", driver.getId());
        model.addAttribute("initialDriverLat", driver.getDriverLat());
        model.addAttribute("initialDriverLon", driver.getDriverLon());
        model.addAttribute("selectedBookingDestinationLat", selectedBooking != null ? selectedBooking.getDropoffLat() : null);
        model.addAttribute("selectedBookingDestinationLon", selectedBooking != null ? selectedBooking.getDropoffLon() : null);
    }

    private Booking resolveSelectedBooking(List<Booking> activeBookings, Integer selectedBookingId) {
        if (activeBookings.isEmpty()) {
            return null;
        }
        if (selectedBookingId == null) {
            return activeBookings.get(0);
        }
        return activeBookings.stream()
                .filter(booking -> booking.getId() == selectedBookingId)
                .findFirst()
                .orElse(activeBookings.get(0));
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
