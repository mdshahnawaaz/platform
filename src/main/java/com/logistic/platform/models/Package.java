package com.logistic.platform.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Package")
public class Package {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  int trackingId;
    private String packageName;
    private PackageCuuentStatus currentStatus;
    private LocalDateTime lastUpdated;

    public Package() {
    }

    public Package(Long id, int trackingId, String packageName, PackageCuuentStatus currentStatus,
            LocalDateTime lastUpdated) {
        this.id = id;
        this.trackingId = trackingId;
        this.packageName = packageName;
        this.currentStatus = currentStatus;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public PackageCuuentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(PackageCuuentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Package{" +
                "id=" + id +
                ", trackingId=" + trackingId +
                ", packageName='" + packageName + '\'' +
                ", currentStatus=" + currentStatus +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
