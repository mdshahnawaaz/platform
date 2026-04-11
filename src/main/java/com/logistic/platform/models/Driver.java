package com.logistic.platform.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "license_number")
    private String licenseNumber;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable=false)
    private String status;

    @OneToOne
    @JoinColumn(name = "current_job_booking_id")
    private Booking currentJob;

    private int rating;

    @Column(nullable=false)
    private Double driverLat;

    @Column(nullable=false)
    private Double driverLon;

    public Driver() {
    }

    public Driver(int id, String name, String licenseNumber, String vehicleType, String vehicleNumber, boolean available,
            String status, Booking currentJob, int rating, Double driverLat, Double driverLon) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.available = available;
        this.status = status;
        this.currentJob = currentJob;
        this.rating = rating;
        this.driverLat = driverLat;
        this.driverLon = driverLon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Booking getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Booking currentJob) {
        this.currentJob = currentJob;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Double getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(Double driverLat) {
        this.driverLat = driverLat;
    }

    public Double getDriverLon() {
        return driverLon;
    }

    public void setDriverLon(Double driverLon) {
        this.driverLon = driverLon;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", available=" + available +
                ", status='" + status + '\'' +
                ", currentJobId=" + (currentJob != null ? currentJob.getId() : null) +
                ", rating=" + rating +
                ", driverLat=" + driverLat +
                ", driverLon=" + driverLon +
                '}';
    }
}
