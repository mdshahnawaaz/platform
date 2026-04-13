package com.logistic.platform.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record AdminDashboardData(
        long totalBookings,
        long completedTrips,
        long activeTrips,
        long pendingBookings,
        long availableDrivers,
        long busyDrivers,
        long totalUsers,
        BigDecimal revenueLast7Days,
        BigDecimal averageOrderValue,
        double fulfillmentRate,
        List<String> weeklyLabels,
        List<Long> weeklyBookingCounts,
        List<BigDecimal> weeklyRevenue,
        Map<String, Long> bookingStatusBreakdown,
        List<Booking> recentBookings,
        List<DriverPerformanceView> topDrivers) {
}
