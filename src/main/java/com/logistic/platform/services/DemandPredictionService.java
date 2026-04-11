package com.logistic.platform.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.logistic.platform.models.BookingStatus;
import com.logistic.platform.models.DemandModelOutput;
import com.logistic.platform.models.DemandPrediction;
import com.logistic.platform.repository.BookingRepository;
import com.logistic.platform.repository.DriverRepository;

@Service
public class DemandPredictionService {

    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(
            BookingStatus.PENDING,
            BookingStatus.UNDER_PROCESS);

    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;
    private final DemandModelIntegrationService demandModelIntegrationService;

    public DemandPredictionService(
            BookingRepository bookingRepository,
            DriverRepository driverRepository,
            DemandModelIntegrationService demandModelIntegrationService) {
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.demandModelIntegrationService = demandModelIntegrationService;
    }

    public DemandPrediction predictDemand(String vehicleType) {
        PredictionContext context = buildPredictionContext(vehicleType);
        Optional<DemandModelOutput> modelOutput = demandModelIntegrationService.scoreLiveDemand(vehicleType);
        if (modelOutput.isPresent()) {
            return buildModelPrediction(context, modelOutput.get());
        }

        return buildHeuristicPrediction(context);
    }

    private PredictionContext buildPredictionContext(String vehicleType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastHour = now.minusHours(1);
        LocalDateTime lastFourWeeks = now.minusWeeks(4);

        long recentBookings = bookingRepository.countByCreatedAtBetweenAndVehicleTypeIgnoreCase(lastHour, now, vehicleType);
        long sameHourHistorical = bookingRepository.countByHourOfDaySince(now.getHour(), lastFourWeeks);
        long sameWeekdayHistorical = bookingRepository.countBySameWeekdayAndHourSince(now, now.getHour(), lastFourWeeks);
        long activeBookings = bookingRepository.countByStatusIn(ACTIVE_STATUSES);
        int availableDrivers = driverRepository.countFreeDrivers();
        return new PredictionContext(recentBookings, sameHourHistorical, sameWeekdayHistorical, activeBookings, availableDrivers);
    }

    private DemandPrediction buildHeuristicPrediction(PredictionContext context) {
        double hourlyAverage = context.sameHourHistorical() / 28.0;
        double weekdayHourlyAverage = context.sameWeekdayHistorical() / 4.0;

        double predictedDemand = (context.recentBookings() * 0.45)
                + (hourlyAverage * 0.25)
                + (weekdayHourlyAverage * 0.20)
                + (context.activeBookings() * 0.10);

        predictedDemand = Math.max(predictedDemand, 1.0);

        BigDecimal demandFactor = clampDemandFactor(
                BigDecimal.valueOf(calculateDemandFactor(predictedDemand, context.availableDrivers()))
                        .setScale(2, RoundingMode.HALF_UP));

        return new DemandPrediction(
                context.recentBookings(),
                context.sameHourHistorical(),
                context.sameWeekdayHistorical(),
                context.activeBookings(),
                context.availableDrivers(),
                round(predictedDemand),
                demandFactor,
                demandFactor.compareTo(new BigDecimal("1.25")) >= 0,
                "heuristic");
    }

    private DemandPrediction buildModelPrediction(PredictionContext context, DemandModelOutput modelOutput) {
        BigDecimal demandFactor = clampDemandFactor(
                modelOutput.recommendedDemandFactor().setScale(2, RoundingMode.HALF_UP));

        return new DemandPrediction(
                context.recentBookings(),
                context.sameHourHistorical(),
                context.sameWeekdayHistorical(),
                context.activeBookings(),
                context.availableDrivers(),
                round(modelOutput.predictedBookingsNext30Minutes()),
                demandFactor,
                demandFactor.compareTo(new BigDecimal("1.25")) >= 0,
                modelOutput.modelSource());
    }

    private BigDecimal clampDemandFactor(BigDecimal demandFactor) {
        if (demandFactor.compareTo(new BigDecimal("0.85")) < 0) {
            return new BigDecimal("0.85");
        }
        if (demandFactor.compareTo(new BigDecimal("3.00")) > 0) {
            return new BigDecimal("3.00");
        }
        return demandFactor;
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double calculateDemandFactor(double predictedDemand, int availableDrivers) {
        if (availableDrivers <= 0) {
            return 3.0;
        }

        double supplyPressure = predictedDemand / availableDrivers;
        if (supplyPressure < 0.45) {
            return 0.90;
        }
        if (supplyPressure < 0.75) {
            return 1.00;
        }
        return 1.0 + (supplyPressure * 0.35);
    }

    private record PredictionContext(
            long recentBookings,
            long sameHourHistorical,
            long sameWeekdayHistorical,
            long activeBookings,
            int availableDrivers) {
    }
}
