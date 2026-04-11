package com.logistic.platform.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "demand_feature_snapshots")
public class DemandFeatureSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "snapshot_time", nullable = false)
    private LocalDateTime snapshotTime;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    public Long getId() {
        return id;
    }

    public DemandFeatureSnapshot(Long id, LocalDateTime snapshotTime, String vehicleType, int hourOfDay, int dayOfWeek,
            boolean weekend, long bookingsLast15Minutes, long bookingsLast30Minutes, long bookingsLast60Minutes,
            long activeBookings, int availableDrivers, BigDecimal averageEstimatedCostLastHour,
            BigDecimal averageTripDistanceLastHour, BigDecimal predictedDemand, BigDecimal currentDemandFactor,
            Long targetBookingsNext30Minutes, boolean labelReady) {
        this.id = id;
        this.snapshotTime = snapshotTime;
        this.vehicleType = vehicleType;
        this.hourOfDay = hourOfDay;
        this.dayOfWeek = dayOfWeek;
        this.weekend = weekend;
        this.bookingsLast15Minutes = bookingsLast15Minutes;
        this.bookingsLast30Minutes = bookingsLast30Minutes;
        this.bookingsLast60Minutes = bookingsLast60Minutes;
        this.activeBookings = activeBookings;
        this.availableDrivers = availableDrivers;
        this.averageEstimatedCostLastHour = averageEstimatedCostLastHour;
        this.averageTripDistanceLastHour = averageTripDistanceLastHour;
        this.predictedDemand = predictedDemand;
        this.currentDemandFactor = currentDemandFactor;
        this.targetBookingsNext30Minutes = targetBookingsNext30Minutes;
        this.labelReady = labelReady;
    }

    public DemandFeatureSnapshot() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSnapshotTime() {
        return snapshotTime;
    }

    public void setSnapshotTime(LocalDateTime snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    public long getBookingsLast15Minutes() {
        return bookingsLast15Minutes;
    }

    public void setBookingsLast15Minutes(long bookingsLast15Minutes) {
        this.bookingsLast15Minutes = bookingsLast15Minutes;
    }

    public long getBookingsLast30Minutes() {
        return bookingsLast30Minutes;
    }

    public void setBookingsLast30Minutes(long bookingsLast30Minutes) {
        this.bookingsLast30Minutes = bookingsLast30Minutes;
    }

    public long getBookingsLast60Minutes() {
        return bookingsLast60Minutes;
    }

    public void setBookingsLast60Minutes(long bookingsLast60Minutes) {
        this.bookingsLast60Minutes = bookingsLast60Minutes;
    }

    public long getActiveBookings() {
        return activeBookings;
    }

    public void setActiveBookings(long activeBookings) {
        this.activeBookings = activeBookings;
    }

    public int getAvailableDrivers() {
        return availableDrivers;
    }

    public void setAvailableDrivers(int availableDrivers) {
        this.availableDrivers = availableDrivers;
    }

    public BigDecimal getAverageEstimatedCostLastHour() {
        return averageEstimatedCostLastHour;
    }

    public void setAverageEstimatedCostLastHour(BigDecimal averageEstimatedCostLastHour) {
        this.averageEstimatedCostLastHour = averageEstimatedCostLastHour;
    }

    public BigDecimal getAverageTripDistanceLastHour() {
        return averageTripDistanceLastHour;
    }

    public void setAverageTripDistanceLastHour(BigDecimal averageTripDistanceLastHour) {
        this.averageTripDistanceLastHour = averageTripDistanceLastHour;
    }

    public BigDecimal getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(BigDecimal predictedDemand) {
        this.predictedDemand = predictedDemand;
    }

    public BigDecimal getCurrentDemandFactor() {
        return currentDemandFactor;
    }

    public void setCurrentDemandFactor(BigDecimal currentDemandFactor) {
        this.currentDemandFactor = currentDemandFactor;
    }

    public Long getTargetBookingsNext30Minutes() {
        return targetBookingsNext30Minutes;
    }

    public void setTargetBookingsNext30Minutes(Long targetBookingsNext30Minutes) {
        this.targetBookingsNext30Minutes = targetBookingsNext30Minutes;
    }

    public boolean isLabelReady() {
        return labelReady;
    }

    public void setLabelReady(boolean labelReady) {
        this.labelReady = labelReady;
    }

    @Column(name = "hour_of_day", nullable = false)
    private int hourOfDay;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Column(name = "is_weekend", nullable = false)
    private boolean weekend;

    @Column(name = "bookings_last_15m", nullable = false)
    private long bookingsLast15Minutes;

    @Column(name = "bookings_last_30m", nullable = false)
    private long bookingsLast30Minutes;

    @Column(name = "bookings_last_60m", nullable = false)
    private long bookingsLast60Minutes;

    @Column(name = "active_bookings", nullable = false)
    private long activeBookings;

    @Column(name = "available_drivers", nullable = false)
    private int availableDrivers;

    @Column(name = "avg_estimated_cost_last_60m", precision = 10, scale = 2)
    private BigDecimal averageEstimatedCostLastHour;

    @Column(name = "avg_trip_distance_last_60m", precision = 10, scale = 2)
    private BigDecimal averageTripDistanceLastHour;

    @Column(name = "predicted_demand", precision = 10, scale = 2)
    private BigDecimal predictedDemand;

    @Column(name = "current_demand_factor", precision = 10, scale = 2)
    private BigDecimal currentDemandFactor;

    @Column(name = "target_bookings_next_30m")
    private Long targetBookingsNext30Minutes;

    @Column(name = "label_ready", nullable = false)
    private boolean labelReady;
}
