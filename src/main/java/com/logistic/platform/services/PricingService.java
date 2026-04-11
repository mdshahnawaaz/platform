package com.logistic.platform.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.logistic.platform.Helper.DistanceCalculator;
import com.logistic.platform.models.DemandPrediction;
import com.logistic.platform.models.PricingQuote;

@Service
public class PricingService {

    public static final BigDecimal BASE_RATE = new BigDecimal("5.00");
    public static final BigDecimal STANDARD_COST_PER_KM = new BigDecimal("2.00");
    public static final BigDecimal PREMIUM_COST_PER_KM = new BigDecimal("3.00");

    private final DemandPredictionService demandPredictionService;

    public PricingService(DemandPredictionService demandPredictionService) {
        this.demandPredictionService = demandPredictionService;
    }

    public PricingQuote buildQuote(
            double pickupLat,
            double pickupLon,
            double dropoffLat,
            double dropoffLon,
            String vehicleType,
            Double demandFactorOverride) {
        double distance = DistanceCalculator.calculateDistance(pickupLat, pickupLon, dropoffLat, dropoffLon);
        return buildQuote(distance, vehicleType, demandFactorOverride);
    }

    public PricingQuote buildQuote(double distance, String vehicleType, Double demandFactorOverride) {
        DemandPrediction demandPrediction = demandPredictionService.predictDemand(vehicleType);
        BigDecimal effectiveDemandFactor = demandFactorOverride != null
                ? BigDecimal.valueOf(demandFactorOverride).setScale(2, RoundingMode.HALF_UP)
                : demandPrediction.demandFactor();
        BigDecimal costPerKm = getCostPerKm(vehicleType);
        BigDecimal estimatedPrice = calculatePrice(distance, costPerKm, effectiveDemandFactor);
        String demandLevel = classifyDemandLevel(effectiveDemandFactor);
        BigDecimal priceAdjustmentPercent = calculateAdjustmentPercent(effectiveDemandFactor);

        return new PricingQuote(
                round(distance),
                vehicleType,
                BASE_RATE,
                costPerKm,
                effectiveDemandFactor,
                demandPrediction.predictedDemand(),
                demandLevel,
                effectiveDemandFactor.compareTo(new BigDecimal("1.25")) >= 0,
                demandPrediction.modelSource(),
                priceAdjustmentPercent,
                buildPricingMessage(demandLevel, priceAdjustmentPercent),
                buildAreaMessage(demandLevel),
                estimatedPrice);
    }

    public BigDecimal calculatePrice(double distance, String vehicleType, double demandFactor) {
        return calculatePrice(distance, getCostPerKm(vehicleType), BigDecimal.valueOf(demandFactor));
    }

    private BigDecimal calculatePrice(double distance, BigDecimal costPerKm, BigDecimal demandFactor) {
        BigDecimal normalizedDistance = BigDecimal.valueOf(distance);
        
        BigDecimal price = BASE_RATE.add(
            costPerKm.multiply(normalizedDistance)
                     .multiply(demandFactor)
        );
        
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getCostPerKm(String vehicleType) {
        return vehicleType.equalsIgnoreCase("premium") ? PREMIUM_COST_PER_KM : STANDARD_COST_PER_KM;
    }

    private String classifyDemandLevel(BigDecimal demandFactor) {
        if (demandFactor.compareTo(new BigDecimal("1.25")) >= 0) {
            return "HIGH";
        }
        if (demandFactor.compareTo(new BigDecimal("0.95")) <= 0) {
            return "LOW";
        }
        return "NORMAL";
    }

    private BigDecimal calculateAdjustmentPercent(BigDecimal demandFactor) {
        return demandFactor.subtract(BigDecimal.ONE)
                .multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private String buildPricingMessage(String demandLevel, BigDecimal adjustmentPercent) {
        if ("HIGH".equals(demandLevel)) {
            return "Due to high demand, prices are currently higher than usual by "
                    + adjustmentPercent.abs().toPlainString() + "%.";
        }
        if ("LOW".equals(demandLevel)) {
            return "Demand is currently low, so this trip includes a "
                    + adjustmentPercent.abs().toPlainString() + "% discount.";
        }
        return "Demand is normal right now, so standard pricing is being applied.";
    }

    private String buildAreaMessage(String demandLevel) {
        if ("HIGH".equals(demandLevel)) {
            return "This route is in a high demand area with heavier booking activity.";
        }
        if ("LOW".equals(demandLevel)) {
            return "This route is in a low demand area, so fares are more affordable right now.";
        }
        return "This route is in a normal demand area with balanced pricing.";
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
