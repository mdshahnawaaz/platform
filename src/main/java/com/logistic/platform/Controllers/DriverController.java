package com.logistic.platform.Controllers;

import java.math.BigDecimal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.BookingStatus;
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

    @GetMapping("/portal")
    public String getDriverPortal(Model model) {
        List<Driver> drivers = driverService.getAllDrivers();
        List<Booking> liveAssignments = driverService.getLiveAssignments();
        long availableDrivers = drivers.stream().filter(Driver::isAvailable).count();
        long engagedDrivers = drivers.size() - availableDrivers;
        long deliveredToday = bookingService.getAllBookings().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.DELIVERED)
                .filter(booking -> booking.getDeliverAt() != null)
                .filter(booking -> booking.getDeliverAt().toLocalDate().equals(java.time.LocalDate.now()))
                .count();

        model.addAttribute("drivers", drivers);
        model.addAttribute("liveAssignments", liveAssignments);
        model.addAttribute("availableDrivers", availableDrivers);
        model.addAttribute("engagedDrivers", engagedDrivers);
        model.addAttribute("deliveredToday", deliveredToday);
        model.addAttribute("activeJobs", liveAssignments.size());
        return "driver_portal";
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
        List<Booking>booking= bookingService.getDriverDetails(id);
        Driver driver = driverService.getDriver(id).orElse(null);
        List<Booking> activeBookingList = booking.stream()
                .filter(item -> item.getStatus() == BookingStatus.PENDING || item.getStatus() == BookingStatus.UNDER_PROCESS)
                .toList();
        long activeBookings = booking.stream()
                .filter(item -> item.getStatus() == BookingStatus.PENDING || item.getStatus() == BookingStatus.UNDER_PROCESS)
                .count();
        long completedBookings = booking.stream()
                .filter(item -> item.getStatus() == BookingStatus.DELIVERED)
                .count();
        BigDecimal totalEarnings = booking.stream()
                .filter(item -> item.getEstimatedCost() != null)
                .map(Booking::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("bookings",booking);
        model.addAttribute("activeBookingList", activeBookingList);
        model.addAttribute("driver", driver);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("totalEarnings", totalEarnings);
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
