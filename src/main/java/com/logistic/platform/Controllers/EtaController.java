package com.logistic.platform.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.platform.models.EtaQuote;
import com.logistic.platform.services.BookingService;
import com.logistic.platform.services.EtaPredictionService;

@RestController
@RequestMapping("/logistics/eta")
public class EtaController {

    private final EtaPredictionService etaPredictionService;
    private final BookingService bookingService;

    public EtaController(EtaPredictionService etaPredictionService, BookingService bookingService) {
        this.etaPredictionService = etaPredictionService;
        this.bookingService = bookingService;
    }

    @GetMapping("/estimate")
    public EtaQuote estimateEta(
            @RequestParam double pickupLat,
            @RequestParam double pickupLon,
            @RequestParam double dropoffLat,
            @RequestParam double dropoffLon,
            @RequestParam String vehicleType) {
        return etaPredictionService.estimateBeforeBooking(
                pickupLat,
                pickupLon,
                dropoffLat,
                dropoffLon,
                vehicleType);
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<EtaQuote> estimateBookingEta(@PathVariable int bookingId) {
        return bookingService.getBooking(bookingId)
                .map(etaPredictionService::estimateForBooking)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
