package com.logistic.platform.models;

public record DriverPerformanceView(
        int id,
        String name,
        String vehicleType,
        String status,
        int rating,
        long completedTrips,
        boolean available) {
}
