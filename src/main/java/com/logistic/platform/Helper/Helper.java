package com.logistic.platform.Helper;

import org.springframework.context.annotation.Configuration;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;

@Configuration
public class Helper {
     private static final double MAX_RATING = 5.0;
    private static final double MAX_DISTANCE = 20.0; 
    private static final double WEIGHT_RATING = 0.4;
    private static final double WEIGHT_DISTANCE = 0.3;
    private static final double WEIGHT_VEHICLE_TYPE_MATCH = 0.1;

    public int calculateScore(Driver driver, Booking booking) {
        double ratingScore = calculateRatingScore(driver.getRating());
        double distanceScore = calculateDistanceScore(driver.getDriverLat(),driver.getDriverLon(),booking.getPickuplat(),booking.getPickuplon());
        double vehicleTypeMatchScore = calculateVehicleTypeMatchScore(driver.getVehicleType(), booking.getVehicleType());

        double totalScore = (WEIGHT_RATING * ratingScore) +
                            (WEIGHT_DISTANCE * distanceScore) +
                            (WEIGHT_VEHICLE_TYPE_MATCH * vehicleTypeMatchScore);

        return (int) Math.round(totalScore * 100); 
    }

    private double calculateRatingScore(double rating) {
        return (rating / MAX_RATING) * 100;
    }

    private double calculateDistanceScore(double driverLat,double driverLon, double pickupLat, double pickupLon) {
        double distance = DistanceCalculator.calculateDistance(driverLat,driverLon,pickupLat,pickupLon);
        return Math.max(0, (MAX_DISTANCE - distance) / MAX_DISTANCE * 100);
    }

    private double calculateVehicleTypeMatchScore(String driverVehicleType, String requestedVehicleType) {
        return driverVehicleType.equals(requestedVehicleType) ? 100 : 0;
    }
}
