package com.logistic.platform.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.logistic.platform.models.Booking;
import com.logistic.platform.services.BookingService;


@Controller
@RequestMapping("/logistics/bookings")
public class BookingController {

    
     @Autowired
    private BookingService bookingService;

    @PostMapping
    public String createBooking(  @RequestParam int userId,
                                                    @RequestParam double pickupLat,
                                                    @RequestParam double pickupLon,
                                                    @RequestParam double dropoffLat,
                                                    @RequestParam double dropoffLon,
                                                    @RequestParam String vehicleType,
                                                    Model model) {    
        Booking booking = bookingService.createBooking(userId, pickupLat, pickupLon, dropoffLat,dropoffLon, vehicleType);
        System.out.println("bookng details are" + booking);
        if(booking.getDriver()!=null)
        {
            model.addAttribute("driverId",booking.getDriver().getId());
            model.addAttribute("driver_vehicleType", booking.getDriver().getVehicleType());
            model.addAttribute("driver_name", booking.getDriver().getName());
            model.addAttribute("driver_rating", booking.getDriver().getRating());
            model.addAttribute("estimate_price", booking.getEstimatedCost());
            return "user_booked";
        }

            model.addAttribute("pickupLat", pickupLat);
            model.addAttribute("pickupLon", pickupLon);
            model.addAttribute("dropoffLat", dropoffLat);
            model.addAttribute("dropoffLon", dropoffLon);
            model.addAttribute("estimate_price", booking.getEstimatedCost());
            return "driver_not_allocated";
        
        // return ResponseEntity.ok(booking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable int id) {
        return bookingService.getBooking(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable int id, @RequestParam String status) {
        Booking updatedBooking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(updatedBooking);
    }
    
    

}
