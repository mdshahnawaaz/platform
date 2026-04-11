package com.logistic.platform.models;

import java.math.BigDecimal;

public record DemandModelInput(
        String vehicleType,
        int hourOfDay,
        int dayOfWeek,
        boolean weekend,
        long bookingsLast15Minutes,
        long bookingsLast30Minutes,
        long bookingsLast60Minutes,
        long activeBookings,
        int availableDrivers,
        BigDecimal averageEstimatedCostLastHour,
        BigDecimal averageTripDistanceLastHour) {
}
