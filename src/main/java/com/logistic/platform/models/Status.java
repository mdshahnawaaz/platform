package com.logistic.platform.models;

public enum Status {

    RESOLVED("Resolved"),
    NOT_RESOLVED("Not Resolved"),
    NOT_SEEN("Not Seen");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
