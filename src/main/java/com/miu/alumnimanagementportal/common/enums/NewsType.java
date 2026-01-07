package com.miu.alumnimanagementportal.common.enums;

public enum NewsType {

    ALUMNI_ACHIEVEMENTS("Alumni Achievements"),
    CAREER_MILESTONES("Career Milestones"),
    AWARDS("Awards & Recognition"),
    STARTUPS("Startups & Entrepreneurship"),
    PUBLICATIONS("Publications & Research"),
    RESEARCH_INNOVATION("Research & Innovation Highlights"),
    CAMPUS_MEMORIES("Fun Campus Memories"),
    THROWBACK_STORIES("Throwback Stories"),
    ANNOUNCEMENT("Announcements"),
    UNIVERSITY_UPDATES("University Updates");

    private final String label;
    NewsType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
