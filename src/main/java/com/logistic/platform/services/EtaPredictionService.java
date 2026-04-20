package com.logistic.platform.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.logistic.platform.Helper.DistanceCalculator;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.DemandPrediction;
import com.logistic.platform.models.Driver;
import com.logistic.platform.models.EtaAccuracySummary;
import com.logistic.platform.models.EtaModelInput;
import com.logistic.platform.models.EtaModelOutput;
import com.logistic.platform.models.EtaQuote;
import com.logistic.platform.models.Location;

@Service
public class EtaPredictionService {

    private final DemandPredictionService demandPredictionService;
    private final EtaModelIntegrationService etaModelIntegrationService;
    private final KafkaService kafkaService;

    public EtaPredictionService(
            DemandPredictionService demandPredictionService,
            EtaModelIntegrationService etaModelIntegrationService,
            KafkaService kafkaService) {
        this.demandPredictionService = demandPredictionService;
        this.etaModelIntegrationService = etaModelIntegrationService;
        this.kafkaService = kafkaService;
    }

    public EtaQuote estimateBeforeBooking(
            double pickupLat,
            double pickupLon,
            double dropoffLat,
            double dropoffLon,
            String vehicleType) {
        return buildQuote(buildLiveInput(
                pickupLat,
                pickupLon,
                dropoffLat,
                dropoffLon,
                vehicleType,
                null,
                LocalDateTime.now()));
    }

    public EtaQuote estimateForBooking(Booking booking) {
        return buildQuote(buildLiveInput(booking));
    }

    public EtaModelInput buildLiveInput(Booking booking) {
        Driver driver = booking.getDriver();
        return buildLiveInput(
                booking.getPickuplat(),
                booking.getPickuplon(),
                booking.getDropoffLat(),
                booking.getDropoffLon(),
                booking.getVehicleType(),
                driver,
                booking.getCreatedAt() != null ? booking.getCreatedAt() : LocalDateTime.now());
    }

    public EtaModelInput buildLiveInput(
            double pickupLat,
            double pickupLon,
            double dropoffLat,
            double dropoffLon,
            String vehicleType,
            Driver driver,
            LocalDateTime requestTime) {
        DemandPrediction demandPrediction = demandPredictionService.predictDemand(vehicleType);
        double tripDistanceKm = DistanceCalculator.calculateDistance(pickupLat, pickupLon, dropoffLat, dropoffLon);
        DriverLocationContext driverContext = resolveDriverLocation(driver, pickupLat, pickupLon);

        return new EtaModelInput(
                vehicleType,
                requestTime.getHour(),
                requestTime.getDayOfWeek().getValue(),
                requestTime.getDayOfWeek().getValue() >= 6,
                round(tripDistanceKm),
                driverContext.distanceToPickupKm(),
                demandPrediction.demandFactor(),
                driver != null,
                driverContext.liveLocationUsed());
    }

    public Optional<EtaModelOutput> scoreLiveEta(Booking booking) {
        return etaModelIntegrationService.scoreEta(buildLiveInput(booking));
    }

    public void applyPredictionSnapshot(Booking booking, EtaQuote etaQuote) {
        booking.setPredictedPickupEtaMinutes(etaQuote.driverArrivalMinutes());
        booking.setPredictedDeliveryEtaMinutes(etaQuote.deliveryMinutes());
        booking.setPredictedTotalEtaMinutes(etaQuote.totalEtaMinutes());
        booking.setPredictedEtaGeneratedAt(LocalDateTime.now());
        booking.setPredictedEtaSource(etaQuote.modelSource());
        booking.setPredictedEtaConfidence(etaQuote.confidence());
    }

    public EtaAccuracySummary buildAccuracySummary(List<Booking> bookings) {
        List<Booking> measuredTrips = bookings.stream()
                .filter(booking -> booking.getPredictedTotalEtaMinutes() != null)
                .filter(booking -> booking.getCreatedAt() != null)
                .filter(booking -> booking.getDeliverAt() != null)
                .toList();

        if (measuredTrips.isEmpty()) {
            return new EtaAccuracySummary(
                    0,
                    BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP),
                    0);
        }

        BigDecimal averagePredicted = BigDecimal.valueOf(measuredTrips.stream()
                .mapToInt(booking -> booking.getPredictedTotalEtaMinutes())
                .average()
                .orElse(0))
                .setScale(1, RoundingMode.HALF_UP);

        BigDecimal averageActual = BigDecimal.valueOf(measuredTrips.stream()
                .mapToLong(booking -> Duration.between(booking.getCreatedAt(), booking.getDeliverAt()).toMinutes())
                .average()
                .orElse(0))
                .setScale(1, RoundingMode.HALF_UP);

        BigDecimal meanAbsoluteError = BigDecimal.valueOf(measuredTrips.stream()
                .mapToDouble(booking -> Math.abs(actualDurationMinutes(booking) - booking.getPredictedTotalEtaMinutes()))
                .average()
                .orElse(0))
                .setScale(1, RoundingMode.HALF_UP);

        double withinTenMinutesRate = BigDecimal.valueOf(measuredTrips.stream()
                .filter(booking -> Math.abs(actualDurationMinutes(booking) - booking.getPredictedTotalEtaMinutes()) <= 10)
                .count() * 100.0 / measuredTrips.size())
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        return new EtaAccuracySummary(
                measuredTrips.size(),
                averagePredicted,
                averageActual,
                meanAbsoluteError,
                withinTenMinutesRate);
    }

    private long actualDurationMinutes(Booking booking) {
        return Duration.between(booking.getCreatedAt(), booking.getDeliverAt()).toMinutes();
    }

    private EtaQuote buildQuote(EtaModelInput modelInput) {
        Optional<EtaModelOutput> modelOutput = etaModelIntegrationService.scoreEta(modelInput);
        if (modelOutput.isPresent()) {
            return buildModelQuote(modelOutput.get(), modelInput.driverToPickupDistanceKm(), modelInput.tripDistanceKm(), modelInput.liveDriverLocationUsed());
        }

        return buildHeuristicQuote(
                modelInput.vehicleType(),
                modelInput.demandFactor(),
                modelInput.driverToPickupDistanceKm(),
                modelInput.tripDistanceKm(),
                modelInput.hourOfDay(),
                modelInput.driverAssigned(),
                modelInput.liveDriverLocationUsed());
    }

    private EtaQuote buildModelQuote(
            EtaModelOutput modelOutput,
            Double driverToPickupDistanceKm,
            double tripDistanceKm,
            boolean liveDriverLocationUsed) {
        int deliveryMinutes = sanitizeMinutes(modelOutput.deliveryMinutes(), 1);
        Integer pickupMinutes = modelOutput.driverArrivalMinutes() == null ? null : sanitizeMinutes(modelOutput.driverArrivalMinutes(), 1);
        int totalMinutes = sanitizeMinutes(
                modelOutput.totalEtaMinutes(),
                pickupMinutes == null ? deliveryMinutes : pickupMinutes + deliveryMinutes);

        return new EtaQuote(
                round(driverToPickupDistanceKm == null ? 0 : driverToPickupDistanceKm),
                round(tripDistanceKm),
                pickupMinutes,
                deliveryMinutes,
                totalMinutes,
                modelOutput.confidence() == null ? "MEDIUM" : modelOutput.confidence(),
                modelOutput.modelSource() == null ? "ml" : modelOutput.modelSource(),
                liveDriverLocationUsed,
                pickupMinutes == null
                        ? "Approximate delivery in " + totalMinutes + " minutes."
                        : "Driver arrival in about " + pickupMinutes + " minutes and delivery in about " + totalMinutes + " minutes.");
    }

    private EtaQuote buildHeuristicQuote(
            String vehicleType,
            BigDecimal demandFactor,
            Double driverToPickupDistanceKm,
            double tripDistanceKm,
            int hourOfDay,
            boolean driverAssigned,
            boolean liveDriverLocationUsed) {
        double trafficMultiplier = trafficMultiplier(hourOfDay);
        double demandMultiplier = Math.max(0.92, demandFactor.doubleValue());

        int deliveryMinutes = estimateLegMinutes(
                tripDistanceKm,
                cruisingSpeed(vehicleType),
                trafficMultiplier,
                demandMultiplier,
                6);

        Integer pickupMinutes = null;
        if (driverAssigned && driverToPickupDistanceKm != null) {
            pickupMinutes = estimateLegMinutes(
                    driverToPickupDistanceKm,
                    pickupSpeed(vehicleType),
                    trafficMultiplier,
                    demandMultiplier,
                    3);
        }

        int totalMinutes = pickupMinutes == null ? deliveryMinutes : pickupMinutes + deliveryMinutes;
        String confidence = determineConfidence(driverAssigned, liveDriverLocationUsed, tripDistanceKm);

        return new EtaQuote(
                round(driverToPickupDistanceKm == null ? 0 : driverToPickupDistanceKm),
                round(tripDistanceKm),
                pickupMinutes,
                deliveryMinutes,
                totalMinutes,
                confidence,
                "heuristic",
                liveDriverLocationUsed,
                pickupMinutes == null
                        ? "Approximate delivery in " + totalMinutes + " minutes."
                        : "Driver arrival in about " + pickupMinutes + " minutes and delivery in about " + totalMinutes + " minutes.");
    }

    private DriverLocationContext resolveDriverLocation(Driver driver, double pickupLat, double pickupLon) {
        if (driver == null) {
            return new DriverLocationContext(null, false);
        }

        Location liveLocation = kafkaService.getDriverLocation(String.valueOf(driver.getId()));
        if (liveLocation != null) {
            double distance = DistanceCalculator.calculateDistance(
                    liveLocation.getLatitude(),
                    liveLocation.getLongitude(),
                    pickupLat,
                    pickupLon);
            return new DriverLocationContext(distance, true);
        }

        double fallbackDistance = DistanceCalculator.calculateDistance(
                driver.getDriverLat(),
                driver.getDriverLon(),
                pickupLat,
                pickupLon);
        return new DriverLocationContext(fallbackDistance, false);
    }

    private int estimateLegMinutes(
            double distanceKm,
            double baseSpeedKmPerHour,
            double trafficMultiplier,
            double demandMultiplier,
            int serviceBufferMinutes) {
        double effectiveSpeed = Math.max(10.0, baseSpeedKmPerHour / (trafficMultiplier * demandMultiplier));
        double driveMinutes = (distanceKm / effectiveSpeed) * 60.0;
        return Math.max(1, (int) Math.ceil(driveMinutes + serviceBufferMinutes));
    }

    private double cruisingSpeed(String vehicleType) {
        return "premium".equalsIgnoreCase(vehicleType) ? 34.0 : 26.0;
    }

    private double pickupSpeed(String vehicleType) {
        return "premium".equalsIgnoreCase(vehicleType) ? 28.0 : 22.0;
    }

    private double trafficMultiplier(int hour) {
        if ((hour >= 8 && hour <= 11) || (hour >= 17 && hour <= 21)) {
            return 1.35;
        }
        if (hour >= 22 || hour <= 5) {
            return 0.90;
        }
        return 1.10;
    }

    private String determineConfidence(boolean driverAssigned, boolean liveDriverLocationUsed, double tripDistanceKm) {
        if (driverAssigned && liveDriverLocationUsed && tripDistanceKm <= 20) {
            return "HIGH";
        }
        if (tripDistanceKm <= 35) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private int sanitizeMinutes(Integer value, int fallback) {
        return value == null ? fallback : Math.max(1, value);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private record DriverLocationContext(Double distanceToPickupKm, boolean liveLocationUsed) {
    }
}
