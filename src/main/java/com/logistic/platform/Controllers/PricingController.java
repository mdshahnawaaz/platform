package com.logistic.platform.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.platform.models.DemandPrediction;
import com.logistic.platform.models.PricingQuote;
import com.logistic.platform.services.PricingService;
import com.logistic.platform.services.DemandPredictionService;

@RestController
@RequestMapping("/logistics/pricing")
public class PricingController {

    private final PricingService pricingService;
    private final DemandPredictionService demandPredictionService;

    public PricingController(PricingService pricingService, DemandPredictionService demandPredictionService) {
        this.pricingService = pricingService;
        this.demandPredictionService = demandPredictionService;
    }

    @GetMapping("/estimate")
    public PricingQuote estimatePrice(
            @RequestParam double pickupLat,
            @RequestParam double pickupLon,
            @RequestParam double dropoffLat,
            @RequestParam double dropoffLon,
            @RequestParam String vehicleType,
            @RequestParam(required = false) Double demandFactor) {
        return pricingService.buildQuote(
                pickupLat,
                pickupLon,
                dropoffLat,
                dropoffLon,
                vehicleType,
                demandFactor);
    }

    @GetMapping("/demand-forecast")
    public DemandPrediction demandForecast(@RequestParam(defaultValue = "standard") String vehicleType) {
        return demandPredictionService.predictDemand(vehicleType);
    }
}
