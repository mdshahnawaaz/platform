package com.logistic.platform.models;

public enum PackageCuuentStatus {

    PICKUP_PENDING("Pickup Pending"),
    IN_TRANSIT("In Transit"),
    OUT_FOR_DELIVERY("Out For Delivery"),
    CANCELLED("Cancelled"),
    DELIVERED("Delivered");


    private final String displayName;

    PackageCuuentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
