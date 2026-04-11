package com.logistic.platform.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.logistic.platform.Helper.DistanceCalculator;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.BookingStatus;
import com.logistic.platform.models.DemandFeatureSnapshot;
import com.logistic.platform.models.DemandPrediction;
import com.logistic.platform.repository.BookingRepository;
import com.logistic.platform.repository.DemandFeatureSnapshotRepository;
import com.logistic.platform.repository.DriverRepository;

@Service
public class DemandFeatureSnapshotService {

    private static final List<String> DEFAULT_VEHICLE_TYPES = List.of("standard", "premium");
    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(
            BookingStatus.PENDING,
            BookingStatus.UNDER_PROCESS);

    private final DemandFeatureSnapshotRepository snapshotRepository;
    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;
    private final DemandPredictionService demandPredictionService;

    public DemandFeatureSnapshotService(
            DemandFeatureSnapshotRepository snapshotRepository,
            BookingRepository bookingRepository,
            DriverRepository driverRepository,
            DemandPredictionService demandPredictionService) {
        this.snapshotRepository = snapshotRepository;
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.demandPredictionService = demandPredictionService;
    }

    public List<DemandFeatureSnapshot> captureDefaultSnapshots() {
        return DEFAULT_VEHICLE_TYPES.stream()
                .map(this::captureSnapshot)
                .toList();
    }

    public DemandFeatureSnapshot captureSnapshot(String vehicleType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last15Minutes = now.minusMinutes(15);
        LocalDateTime last30Minutes = now.minusMinutes(30);
        LocalDateTime last60Minutes = now.minusHours(1);

        List<Booking> lastHourBookings = getBookingsBetween(last60Minutes, now, vehicleType);
        DemandPrediction demandPrediction = demandPredictionService.predictDemand(vehicleType);

        DemandFeatureSnapshot snapshot = new DemandFeatureSnapshot();
        snapshot.setSnapshotTime(now);
        snapshot.setVehicleType(vehicleType.toLowerCase());
        snapshot.setHourOfDay(now.getHour());
        snapshot.setDayOfWeek(now.getDayOfWeek().getValue());
        snapshot.setWeekend(isWeekend(now.getDayOfWeek()));
        snapshot.setBookingsLast15Minutes(countBookingsBetween(last15Minutes, now, vehicleType));
        snapshot.setBookingsLast30Minutes(countBookingsBetween(last30Minutes, now, vehicleType));
        snapshot.setBookingsLast60Minutes(countBookingsBetween(last60Minutes, now, vehicleType));
        snapshot.setActiveBookings(bookingRepository.countByStatusIn(ACTIVE_STATUSES));
        snapshot.setAvailableDrivers(driverRepository.countFreeDrivers());
        snapshot.setAverageEstimatedCostLastHour(averageEstimatedCost(lastHourBookings));
        snapshot.setAverageTripDistanceLastHour(averageDistance(lastHourBookings));
        snapshot.setPredictedDemand(BigDecimal.valueOf(demandPrediction.predictedDemand()).setScale(2, RoundingMode.HALF_UP));
        snapshot.setCurrentDemandFactor(demandPrediction.demandFactor());
        snapshot.setLabelReady(false);

        return snapshotRepository.save(snapshot);
    }

    public int backfillLabels() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        List<DemandFeatureSnapshot> unlabeledSnapshots = snapshotRepository.findByLabelReadyFalseAndSnapshotTimeBefore(cutoff);

        unlabeledSnapshots.forEach(snapshot -> {
            LocalDateTime targetEnd = snapshot.getSnapshotTime().plusMinutes(30);
            long futureDemand = countBookingsBetween(snapshot.getSnapshotTime(), targetEnd, snapshot.getVehicleType());
            snapshot.setTargetBookingsNext30Minutes(futureDemand);
            snapshot.setLabelReady(true);
        });

        snapshotRepository.saveAll(unlabeledSnapshots);
        return unlabeledSnapshots.size();
    }

    public List<DemandFeatureSnapshot> getRecentSnapshots() {
        return snapshotRepository.findTop50ByOrderBySnapshotTimeDesc();
    }

    @Scheduled(cron = "${ai.feature-snapshot.capture-cron:0 */15 * * * *}")
    public void scheduledCapture() {
        captureDefaultSnapshots();
    }

    @Scheduled(cron = "${ai.feature-snapshot.label-cron:0 */15 * * * *}")
    public void scheduledLabelBackfill() {
        backfillLabels();
    }

    private long countBookingsBetween(LocalDateTime start, LocalDateTime end, String vehicleType) {
        if (isAllVehicles(vehicleType)) {
            return bookingRepository.countByCreatedAtBetween(start, end);
        }
        return bookingRepository.countByCreatedAtBetweenAndVehicleTypeIgnoreCase(start, end, vehicleType);
    }

    private List<Booking> getBookingsBetween(LocalDateTime start, LocalDateTime end, String vehicleType) {
        if (isAllVehicles(vehicleType)) {
            return bookingRepository.findByCreatedAtBetween(start, end);
        }
        return bookingRepository.findByCreatedAtBetweenAndVehicleTypeIgnoreCase(start, end, vehicleType);
    }

    private boolean isAllVehicles(String vehicleType) {
        return vehicleType == null || vehicleType.isBlank() || "all".equalsIgnoreCase(vehicleType);
    }

    private boolean isWeekend(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private BigDecimal averageEstimatedCost(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal total = bookings.stream()
                .map(Booking::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(bookings.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal averageDistance(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        double totalDistance = bookings.stream()
                .mapToDouble(booking -> DistanceCalculator.calculateDistance(
                        booking.getPickuplat(),
                        booking.getPickuplon(),
                        booking.getDropoffLat(),
                        booking.getDropoffLon()))
                .sum();

        return BigDecimal.valueOf(totalDistance / bookings.size()).setScale(2, RoundingMode.HALF_UP);
    }
}
