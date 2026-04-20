package com.logistic.platform.models;

public record EtaQuote(
        double driverToPickupDistanceKm,
        double tripDistanceKm,
        Integer driverArrivalMinutes,
        int deliveryMinutes,
        int totalEtaMinutes,
        String confidence,
        String modelSource,
        boolean liveDriverLocationUsed,
        String summaryMessage) {
}
