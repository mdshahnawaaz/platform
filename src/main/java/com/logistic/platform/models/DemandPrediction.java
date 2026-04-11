package com.logistic.platform.models;

import java.math.BigDecimal;

public record DemandPrediction(
        long recentBookings,
        long sameHourHistoricalBookings,
        long sameWeekdayHistoricalBookings,
        long activeBookings,
        int availableDrivers,
        double predictedDemand,
        BigDecimal demandFactor,
        boolean surgeExpected,
        String modelSource) {
}
