package com.logistic.platform.models;

import java.math.BigDecimal;

public record EtaModelInput(
        String vehicleType,
        int hourOfDay,
        int dayOfWeek,
        boolean weekend,
        double tripDistanceKm,
        Double driverToPickupDistanceKm,
        BigDecimal demandFactor,
        boolean driverAssigned,
        boolean liveDriverLocationUsed) {
}
