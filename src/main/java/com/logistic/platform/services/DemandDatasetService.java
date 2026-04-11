package com.logistic.platform.services;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.logistic.platform.models.DemandFeatureSnapshot;
import com.logistic.platform.models.DemandTrainingSpec;
import com.logistic.platform.repository.DemandFeatureSnapshotRepository;

@Service
public class DemandDatasetService {

    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final DemandFeatureSnapshotRepository snapshotRepository;

    public DemandDatasetService(DemandFeatureSnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    public String exportSnapshotsAsCsv(boolean labeledOnly) {
        List<DemandFeatureSnapshot> snapshots = snapshotRepository.findAll();
        if (labeledOnly) {
            snapshots = snapshots.stream()
                    .filter(DemandFeatureSnapshot::isLabelReady)
                    .collect(Collectors.toList());
        }

        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",",
                "snapshot_time",
                "vehicle_type",
                "hour_of_day",
                "day_of_week",
                "is_weekend",
                "bookings_last_15m",
                "bookings_last_30m",
                "bookings_last_60m",
                "active_bookings",
                "available_drivers",
                "avg_estimated_cost_last_60m",
                "avg_trip_distance_last_60m",
                "predicted_demand",
                "current_demand_factor",
                "target_bookings_next_30m",
                "label_ready"))
                .append('\n');

        for (DemandFeatureSnapshot snapshot : snapshots) {
            csv.append(CSV_DATE_FORMAT.format(snapshot.getSnapshotTime())).append(',')
                    .append(snapshot.getVehicleType()).append(',')
                    .append(snapshot.getHourOfDay()).append(',')
                    .append(snapshot.getDayOfWeek()).append(',')
                    .append(snapshot.isWeekend()).append(',')
                    .append(snapshot.getBookingsLast15Minutes()).append(',')
                    .append(snapshot.getBookingsLast30Minutes()).append(',')
                    .append(snapshot.getBookingsLast60Minutes()).append(',')
                    .append(snapshot.getActiveBookings()).append(',')
                    .append(snapshot.getAvailableDrivers()).append(',')
                    .append(snapshot.getAverageEstimatedCostLastHour()).append(',')
                    .append(snapshot.getAverageTripDistanceLastHour()).append(',')
                    .append(snapshot.getPredictedDemand()).append(',')
                    .append(snapshot.getCurrentDemandFactor()).append(',')
                    .append(snapshot.getTargetBookingsNext30Minutes() != null ? snapshot.getTargetBookingsNext30Minutes() : "")
                    .append(',')
                    .append(snapshot.isLabelReady())
                    .append('\n');
        }

        return csv.toString();
    }

    public DemandTrainingSpec getTrainingSpec() {
        return new DemandTrainingSpec(
                List.of(
                        "hour_of_day",
                        "day_of_week",
                        "is_weekend",
                        "bookings_last_15m",
                        "bookings_last_30m",
                        "bookings_last_60m",
                        "active_bookings",
                        "available_drivers",
                        "avg_estimated_cost_last_60m",
                        "avg_trip_distance_last_60m",
                        "current_demand_factor",
                        "vehicle_type"),
                "target_bookings_next_30m",
                "regression",
                "next 30 minutes",
                "Use labeled rows only where label_ready=true. Vehicle type should be one-hot encoded in training.");
    }
}
