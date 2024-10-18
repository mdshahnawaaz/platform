package com.logistic.platform.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.platform.models.Booking;
import com.logistic.platform.services.BookingService;

@RestController
@RequestMapping("/logistics/bookings")
public class BookingController {

    
     @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(  @RequestParam int userId,
                                                    @RequestParam double pickupLat,
                                                    @RequestParam double pickupLon,
                                                    @RequestParam double dropoffLat,
                                                    @RequestParam double dropoffLon,
                                                    @RequestParam String vehicleType) {
        Booking booking = bookingService.createBooking(userId, pickupLat, pickupLon, dropoffLat,dropoffLon, vehicleType);
        System.out.println("bookng details are" + booking);
        return ResponseEntity.ok(booking);
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
