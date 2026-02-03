package com.miu.alumnimanagementportal.funding.dtos;

import com.miu.alumnimanagementportal.funding.enums.CampaignStatus;

import java.math.BigDecimal;
import java.util.Date;

public class FundingCampaignDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String title;
    private String description;
    private BigDecimal goalAmount;
    private Date startDate;
    private Date endDate;
    private CampaignStatus status;
    private String contactPhone;
    private boolean cardEnabled;
    private boolean bkashEnabled;
    private BigDecimal raisedAmount;
    private Double percentRaised;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
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
    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public boolean isCardEnabled() { return cardEnabled; }
    public void setCardEnabled(boolean cardEnabled) { this.cardEnabled = cardEnabled; }
    public boolean isBkashEnabled() { return bkashEnabled; }
    public void setBkashEnabled(boolean bkashEnabled) { this.bkashEnabled = bkashEnabled; }
    public BigDecimal getRaisedAmount() { return raisedAmount; }
    public void setRaisedAmount(BigDecimal raisedAmount) { this.raisedAmount = raisedAmount; }
    public Double getPercentRaised() { return percentRaised; }
    public void setPercentRaised(Double percentRaised) { this.percentRaised = percentRaised; }
}
