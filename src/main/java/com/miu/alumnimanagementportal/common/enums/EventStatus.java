package com.miu.alumnimanagementportal.common.enums;

public enum EventStatus {
    UPCOMING("Upcoming"),
    ONGOING("Ongoing"),
    PAST("Past");

    private final String label;

    EventStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
