package com.miu.alumnimanagementportal.common.enums;

public enum EventType {
    ALUMNI_MEETUPS("🎓 Alumni Meetups"),
    CAREER_JOB_FAIR("💼 Career & Job Fair"),
    WORKSHOPS_WEBINARS("🧠 Workshops / Webinars"),
    NETWORKING_EVENTS("🌍 Networking Events"),
    UNIVERSITY_PROGRAMS("🏫 University Programs"),
    STARTUP_TECH_TALKS("🚀 Startup / Tech Talks"),
    REUNION_SOCIAL_EVENTS("❤️ Reunion & Social Events");

    private final String label;

    EventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
