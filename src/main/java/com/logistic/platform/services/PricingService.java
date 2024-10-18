package com.logistic.platform.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class PricingService {

    private static final BigDecimal BASE_RATE = new BigDecimal("5.00");
    private static final BigDecimal STANDARD_COST_PER_KM = new BigDecimal("2.00");
    private static final BigDecimal PREMIUM_COST_PER_KM = new BigDecimal("3.00");

    public BigDecimal calculatePrice(double distance, String vehicleType, double demandFactor) {
        BigDecimal costPerKm = vehicleType.equalsIgnoreCase("premium") ? PREMIUM_COST_PER_KM : STANDARD_COST_PER_KM;
        
        BigDecimal price = BASE_RATE.add(
            costPerKm.multiply(BigDecimal.valueOf(distance))
                     .multiply(BigDecimal.valueOf(demandFactor))
        );
        
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
