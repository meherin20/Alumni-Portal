package com.miu.alumnimanagementportal.funding.dtos;

import java.math.BigDecimal;

public class DonationCreateResultDto {
    private Long donationId;
    private Long campaignId;
    private BigDecimal amount;
    private String paymentUrl; // redirect URL for gateway (mock or real)
    private String message;

    public Long getDonationId() { return donationId; }
    public void setDonationId(Long donationId) { this.donationId = donationId; }
    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
