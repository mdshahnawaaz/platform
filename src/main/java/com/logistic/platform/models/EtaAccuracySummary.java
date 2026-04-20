package com.logistic.platform.models;

import java.math.BigDecimal;

public record EtaAccuracySummary(
        long measuredTrips,
        BigDecimal averagePredictedMinutes,
        BigDecimal averageActualMinutes,
        BigDecimal meanAbsoluteErrorMinutes,
        double withinTenMinutesRate) {
}
