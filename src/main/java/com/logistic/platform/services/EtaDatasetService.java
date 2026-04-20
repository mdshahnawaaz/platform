package com.logistic.platform.services;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.logistic.platform.Helper.DistanceCalculator;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.EtaTrainingSpec;
import com.logistic.platform.repository.BookingRepository;

@Service
public class EtaDatasetService {

    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final BookingRepository bookingRepository;

    public EtaDatasetService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public String exportBookingsAsCsv(boolean completedOnly) {
        List<Booking> bookings = bookingRepository.findAll();
        if (completedOnly) {
            bookings = bookings.stream()
                    .filter(booking -> booking.getCreatedAt() != null)
                    .filter(booking -> booking.getDeliverAt() != null)
                    .collect(Collectors.toList());
        }

        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",",
                "booking_id",
                "created_at",
                "delivered_at",
                "vehicle_type",
                "hour_of_day",
                "day_of_week",
                "is_weekend",
                "trip_distance_km",
                "driver_to_pickup_distance_km",
                "demand_factor",
                "driver_assigned",
                "live_driver_location_used",
                "predicted_pickup_eta_minutes",
                "predicted_delivery_eta_minutes",
                "predicted_total_eta_minutes",
                "predicted_eta_source",
                "predicted_eta_confidence",
                "actual_total_duration_minutes",
                "label_ready"))
                .append('\n');

        for (Booking booking : bookings) {
            double tripDistanceKm = DistanceCalculator.calculateDistance(
                    booking.getPickuplat(),
                    booking.getPickuplon(),
                    booking.getDropoffLat(),
                    booking.getDropoffLon());

            String driverToPickupDistance = "";
            if (booking.getDriver() != null) {
                double distance = DistanceCalculator.calculateDistance(
                        booking.getDriver().getDriverLat(),
                        booking.getDriver().getDriverLon(),
                        booking.getPickuplat(),
                        booking.getPickuplon());
                driverToPickupDistance = String.format("%.2f", distance);
            }

            String actualDuration = "";
            boolean labelReady = booking.getCreatedAt() != null && booking.getDeliverAt() != null;
            if (labelReady) {
                actualDuration = String.valueOf(Duration.between(booking.getCreatedAt(), booking.getDeliverAt()).toMinutes());
            }

            csv.append(booking.getId()).append(',')
                    .append(booking.getCreatedAt() != null ? CSV_DATE_FORMAT.format(booking.getCreatedAt()) : "").append(',')
                    .append(booking.getDeliverAt() != null ? CSV_DATE_FORMAT.format(booking.getDeliverAt()) : "").append(',')
                    .append(booking.getVehicleType() != null ? booking.getVehicleType() : "").append(',')
                    .append(booking.getCreatedAt() != null ? booking.getCreatedAt().getHour() : "").append(',')
                    .append(booking.getCreatedAt() != null ? booking.getCreatedAt().getDayOfWeek().getValue() : "").append(',')
                    .append(booking.getCreatedAt() != null && booking.getCreatedAt().getDayOfWeek().getValue() >= 6).append(',')
                    .append(String.format("%.2f", tripDistanceKm)).append(',')
                    .append(driverToPickupDistance).append(',')
                    .append(estimateDemandFactor(booking)).append(',')
                    .append(booking.getDriver() != null).append(',')
                    .append("HIGH".equalsIgnoreCase(booking.getPredictedEtaConfidence())).append(',')
                    .append(booking.getPredictedPickupEtaMinutes() != null ? booking.getPredictedPickupEtaMinutes() : "").append(',')
                    .append(booking.getPredictedDeliveryEtaMinutes() != null ? booking.getPredictedDeliveryEtaMinutes() : "").append(',')
                    .append(booking.getPredictedTotalEtaMinutes() != null ? booking.getPredictedTotalEtaMinutes() : "").append(',')
                    .append(booking.getPredictedEtaSource() != null ? booking.getPredictedEtaSource() : "").append(',')
                    .append(booking.getPredictedEtaConfidence() != null ? booking.getPredictedEtaConfidence() : "").append(',')
                    .append(actualDuration).append(',')
                    .append(labelReady)
                    .append('\n');
        }

        return csv.toString();
    }

    public EtaTrainingSpec getTrainingSpec() {
        return new EtaTrainingSpec(
                List.of(
                        "hour_of_day",
                        "day_of_week",
                        "is_weekend",
                        "trip_distance_km",
                        "driver_to_pickup_distance_km",
                        "demand_factor",
                        "driver_assigned",
                        "live_driver_location_used",
                        "vehicle_type"),
                "actual_total_duration_minutes",
                "regression",
                "current booking to delivery completion",
                "Use completed rows only where label_ready=true. driver_to_pickup_distance_km may be blank when no driver was assigned yet.");
    }

    private String estimateDemandFactor(Booking booking) {
        if (booking.getPredictedTotalEtaMinutes() == null || booking.getPredictedDeliveryEtaMinutes() == null) {
            return "1.00";
        }
        double ratio = (double) booking.getPredictedTotalEtaMinutes() / Math.max(booking.getPredictedDeliveryEtaMinutes(), 1);
        return String.format("%.2f", Math.max(0.85, Math.min(3.0, ratio)));
    }
}
