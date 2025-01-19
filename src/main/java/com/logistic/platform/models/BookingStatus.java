package com.logistic.platform.models;

public enum BookingStatus {

    PENDING("Pending"),
    UNDER_PROCESS("Under Process"),
    DELIVERED("Delivered");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
