package com.miu.alumnimanagementportal.funding.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class CampaignCreateRequest {
    private Long eventId;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal goalAmount;
    private Date startDate;
    private Date endDate;
    private String contactPhone;
    private boolean cardEnabled = true;
    private boolean bkashEnabled = true;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getGoalAmount() { return goalAmount; }
    public void setGoalAmount(BigDecimal goalAmount) { this.goalAmount = goalAmount; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public boolean isCardEnabled() { return cardEnabled; }
    public void setCardEnabled(boolean cardEnabled) { this.cardEnabled = cardEnabled; }
    public boolean isBkashEnabled() { return bkashEnabled; }
    public void setBkashEnabled(boolean bkashEnabled) { this.bkashEnabled = bkashEnabled; }
}
