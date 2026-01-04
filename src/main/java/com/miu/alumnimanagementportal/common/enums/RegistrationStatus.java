package com.miu.alumnimanagementportal.common.enums;

public enum RegistrationStatus {
    REGISTER_NOW("Register Now"),
    APPLIED("Applied"),
    CLOSED("Closed");

    private final String label;

    RegistrationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
