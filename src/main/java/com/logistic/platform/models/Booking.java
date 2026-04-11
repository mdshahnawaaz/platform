package com.logistic.platform.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {

     @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(name = "pickup_lat", nullable = false)
    private double pickuplat;

    @Column(name = "pickup_lon", nullable = false)
    private double pickuplon;

    @Column(name = "dropoff_lat", nullable = false)
    private double dropoffLat;

    @Column(name = "dropoff_lon", nullable = false)
    private double dropoffLon;

    @Column(nullable = false)
    private BigDecimal estimatedCost;

    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deliver_at")
    private LocalDateTime deliverAt;

    @Column(name = "vehicle_type")
    private String vehicleType;

    public Booking() {
    }

    public Booking(int id, User user, Driver driver, double pickuplat, double pickuplon, double dropoffLat,
            double dropoffLon, BigDecimal estimatedCost, BookingStatus status, LocalDateTime createdAt,
            LocalDateTime deliverAt, String vehicleType) {
        this.id = id;
        this.user = user;
        this.driver = driver;
        this.pickuplat = pickuplat;
        this.pickuplon = pickuplon;
        this.dropoffLat = dropoffLat;
        this.dropoffLon = dropoffLon;
        this.estimatedCost = estimatedCost;
        this.status = status;
        this.createdAt = createdAt;
        this.deliverAt = deliverAt;
        this.vehicleType = vehicleType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public double getPickuplat() {
        return pickuplat;
    }

    public void setPickuplat(double pickuplat) {
        this.pickuplat = pickuplat;
    }

    public double getPickuplon() {
        return pickuplon;
    }

    public void setPickuplon(double pickuplon) {
        this.pickuplon = pickuplon;
    }

    public double getDropoffLat() {
        return dropoffLat;
    }

    public void setDropoffLat(double dropoffLat) {
        this.dropoffLat = dropoffLat;
    }

    public double getDropoffLon() {
        return dropoffLon;
    }

    public void setDropoffLon(double dropoffLon) {
        this.dropoffLon = dropoffLon;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeliverAt() {
        return deliverAt;
    }

    public void setDeliverAt(LocalDateTime deliverAt) {
        this.deliverAt = deliverAt;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", driverId=" + (driver != null ? driver.getId() : null) +
                ", pickuplat=" + pickuplat +
                ", pickuplon=" + pickuplon +
                ", dropoffLat=" + dropoffLat +
                ", dropoffLon=" + dropoffLon +
                ", estimatedCost=" + estimatedCost +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", deliverAt=" + deliverAt +
                ", vehicleType='" + vehicleType + '\'' +
                '}';
    }
}
