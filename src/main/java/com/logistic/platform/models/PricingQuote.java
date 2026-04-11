package com.logistic.platform.models;

import java.math.BigDecimal;

public record PricingQuote(
        double distanceKm,
        String vehicleType,
        BigDecimal baseRate,
        BigDecimal costPerKm,
        BigDecimal demandFactor,
        double predictedDemand,
        String demandLevel,
        boolean surgeExpected,
        String modelSource,
        BigDecimal priceAdjustmentPercent,
        String pricingMessage,
        String areaMessage,
        BigDecimal estimatedPrice) {
}
