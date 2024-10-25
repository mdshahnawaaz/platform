package com.logistic.platform.Controllers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.platform.Helper.DistanceCalculator;
import com.logistic.platform.services.PricingService;

@RestController
@RequestMapping("/logistics/pricing")
public class PricingController {

     @Autowired
    private PricingService pricingService;

    @GetMapping("/estimate")
    public BigDecimal estimatePrice(Model model,
            @RequestParam double pickupLat,
            @RequestParam double pickupLon,
            @RequestParam double dropoffLat,
            @RequestParam double dropoffLon,
            @RequestParam String vehicleType,
            @RequestParam(defaultValue = "1.0") double demandFactor) {
        
        double distance = DistanceCalculator.calculateDistance(pickupLat, pickupLon, dropoffLat, dropoffLon);
        BigDecimal d= pricingService.calculatePrice(distance, vehicleType, demandFactor);
        
        return d;
    }
}
