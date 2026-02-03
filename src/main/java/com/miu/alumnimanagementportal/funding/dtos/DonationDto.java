package com.miu.alumnimanagementportal.funding.dtos;

import com.miu.alumnimanagementportal.funding.enums.DonationStatus;
import com.miu.alumnimanagementportal.funding.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.Date;

public class DonationDto {
    private Long id;
    private Long campaignId;
    private String campaignTitle;
    private Long userId;
    private String userEmail;
    private String userName;
    private BigDecimal amount;
    private PaymentMethod method;
    private DonationStatus status;
    private Date createdDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
    public String getCampaignTitle() { return campaignTitle; }
    public void setCampaignTitle(String campaignTitle) { this.campaignTitle = campaignTitle; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public DonationStatus getStatus() { return status; }
    public void setStatus(DonationStatus status) { this.status = status; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
