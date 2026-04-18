package com.logistic.platform.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.logistic.platform.models.DemandModelInput;
import com.logistic.platform.models.DemandModelOutput;
import com.logistic.platform.models.DemandModelStatus;
import com.logistic.platform.repository.BookingRepository;
import com.logistic.platform.repository.DriverRepository;

@Service
public class DemandModelIntegrationService {

    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;
    private final RestClient restClient;
    private final AtomicReference<String> lastAttemptAt = new AtomicReference<>(null);
    private final AtomicReference<String> lastSuccessAt = new AtomicReference<>(null);
    private final AtomicReference<String> lastSuccessModelSource = new AtomicReference<>(null);
    private final AtomicReference<String> lastError = new AtomicReference<>(null);

    @Value("${ai.model.enabled:false}")
    private boolean modelEnabled;

    @Value("${ai.model.base-url:http://localhost:8000}")
    private String modelBaseUrl;

    public DemandModelIntegrationService(
            BookingRepository bookingRepository,
            DriverRepository driverRepository,
            RestClient.Builder restClientBuilder) {
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.restClient = restClientBuilder.build();
    }

    public DemandModelInput buildLiveInput(String vehicleType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last15Minutes = now.minusMinutes(15);
        LocalDateTime last30Minutes = now.minusMinutes(30);
        LocalDateTime last60Minutes = now.minusHours(1);

        return new DemandModelInput(
                vehicleType,
                now.getHour(),
                now.getDayOfWeek().getValue(),
                now.getDayOfWeek().getValue() >= 6,
                bookingRepository.countByCreatedAtBetweenAndVehicleTypeIgnoreCase(last15Minutes, now, vehicleType),
                bookingRepository.countByCreatedAtBetweenAndVehicleTypeIgnoreCase(last30Minutes, now, vehicleType),
                bookingRepository.countByCreatedAtBetweenAndVehicleTypeIgnoreCase(last60Minutes, now, vehicleType),
                bookingRepository.countByStatusIn(java.util.List.of(
                        com.logistic.platform.models.BookingStatus.PENDING,
                        com.logistic.platform.models.BookingStatus.UNDER_PROCESS)),
                driverRepository.countFreeDrivers(),
                averageEstimatedCostLastHour(vehicleType, last60Minutes, now),
                averageDistanceLastHour(vehicleType, last60Minutes, now));
    }

    public Optional<DemandModelOutput> scoreLiveDemand(String vehicleType) {
        lastAttemptAt.set(LocalDateTime.now().toString());
        if (!modelEnabled) {
            lastError.set("ai.model.enabled=false");
            return Optional.empty();
        }

        try {
            DemandModelInput input = buildLiveInput(vehicleType);
            DemandModelOutput output = restClient.post()
                    .uri(modelBaseUrl + "/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(input)
                    .retrieve()
                    .body(DemandModelOutput.class);

            if (output != null) {
                lastSuccessAt.set(LocalDateTime.now().toString());
                lastSuccessModelSource.set(output.modelSource());
                lastError.set(null);
            }
            return Optional.ofNullable(output);
        } catch (Exception ex) {
            lastError.set(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            return Optional.empty();
        }
    }

    public DemandModelStatus getStatus() {
        return new DemandModelStatus(
                modelEnabled,
                modelBaseUrl,
                lastAttemptAt.get(),
                lastSuccessAt.get(),
                lastSuccessModelSource.get(),
                lastError.get());
    }

    private BigDecimal averageEstimatedCostLastHour(String vehicleType, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByCreatedAtBetweenAndVehicleTypeIgnoreCase(start, end, vehicleType).stream()
                .map(booking -> booking.getEstimatedCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(Math.max(
                                bookingRepository.findByCreatedAtBetweenAndVehicleTypeIgnoreCase(start, end, vehicleType).size(),
                                1)),
                        2,
                        java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal averageDistanceLastHour(String vehicleType, LocalDateTime start, LocalDateTime end) {
        var bookings = bookingRepository.findByCreatedAtBetweenAndVehicleTypeIgnoreCase(start, end, vehicleType);
        if (bookings.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
        }
        double totalDistance = bookings.stream()
                .mapToDouble(booking -> com.logistic.platform.Helper.DistanceCalculator.calculateDistance(
                        booking.getPickuplat(),
                        booking.getPickuplon(),
                        booking.getDropoffLat(),
                        booking.getDropoffLon()))
                .sum();
        return BigDecimal.valueOf(totalDistance / bookings.size()).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
