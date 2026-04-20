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

    @Column(name = "delivery_verification_code_hash")
    private String deliveryVerificationCodeHash;

    @Column(name = "delivery_verification_expires_at")
    private LocalDateTime deliveryVerificationExpiresAt;

    @Column(name = "delivery_verification_sent_at")
    private LocalDateTime deliveryVerificationSentAt;

    @Column(name = "predicted_pickup_eta_minutes")
    private Integer predictedPickupEtaMinutes;

    @Column(name = "predicted_delivery_eta_minutes")
    private Integer predictedDeliveryEtaMinutes;

    @Column(name = "predicted_total_eta_minutes")
    private Integer predictedTotalEtaMinutes;

    @Column(name = "predicted_eta_generated_at")
    private LocalDateTime predictedEtaGeneratedAt;

    @Column(name = "predicted_eta_source")
    private String predictedEtaSource;

    @Column(name = "predicted_eta_confidence")
    private String predictedEtaConfidence;

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

    public String getDeliveryVerificationCodeHash() {
        return deliveryVerificationCodeHash;
    }

    public void setDeliveryVerificationCodeHash(String deliveryVerificationCodeHash) {
        this.deliveryVerificationCodeHash = deliveryVerificationCodeHash;
    }

    public LocalDateTime getDeliveryVerificationExpiresAt() {
        return deliveryVerificationExpiresAt;
    }

    public void setDeliveryVerificationExpiresAt(LocalDateTime deliveryVerificationExpiresAt) {
        this.deliveryVerificationExpiresAt = deliveryVerificationExpiresAt;
    }

    public LocalDateTime getDeliveryVerificationSentAt() {
        return deliveryVerificationSentAt;
    }

    public void setDeliveryVerificationSentAt(LocalDateTime deliveryVerificationSentAt) {
        this.deliveryVerificationSentAt = deliveryVerificationSentAt;
    }

    public Integer getPredictedPickupEtaMinutes() {
        return predictedPickupEtaMinutes;
    }

    public void setPredictedPickupEtaMinutes(Integer predictedPickupEtaMinutes) {
        this.predictedPickupEtaMinutes = predictedPickupEtaMinutes;
    }

    public Integer getPredictedDeliveryEtaMinutes() {
        return predictedDeliveryEtaMinutes;
    }

    public void setPredictedDeliveryEtaMinutes(Integer predictedDeliveryEtaMinutes) {
        this.predictedDeliveryEtaMinutes = predictedDeliveryEtaMinutes;
    }

    public Integer getPredictedTotalEtaMinutes() {
        return predictedTotalEtaMinutes;
    }

    public void setPredictedTotalEtaMinutes(Integer predictedTotalEtaMinutes) {
        this.predictedTotalEtaMinutes = predictedTotalEtaMinutes;
    }

    public LocalDateTime getPredictedEtaGeneratedAt() {
        return predictedEtaGeneratedAt;
    }

    public void setPredictedEtaGeneratedAt(LocalDateTime predictedEtaGeneratedAt) {
        this.predictedEtaGeneratedAt = predictedEtaGeneratedAt;
    }

    public String getPredictedEtaSource() {
        return predictedEtaSource;
    }

    public void setPredictedEtaSource(String predictedEtaSource) {
        this.predictedEtaSource = predictedEtaSource;
    }

    public String getPredictedEtaConfidence() {
        return predictedEtaConfidence;
    }

    public void setPredictedEtaConfidence(String predictedEtaConfidence) {
        this.predictedEtaConfidence = predictedEtaConfidence;
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
                ", predictedPickupEtaMinutes=" + predictedPickupEtaMinutes +
                ", predictedDeliveryEtaMinutes=" + predictedDeliveryEtaMinutes +
                ", predictedTotalEtaMinutes=" + predictedTotalEtaMinutes +
                ", predictedEtaGeneratedAt=" + predictedEtaGeneratedAt +
                ", predictedEtaSource='" + predictedEtaSource + '\'' +
                ", deliveryVerificationExpiresAt=" + deliveryVerificationExpiresAt +
                '}';
    }
}
