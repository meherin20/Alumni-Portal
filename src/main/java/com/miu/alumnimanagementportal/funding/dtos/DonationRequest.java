package com.miu.alumnimanagementportal.funding.dtos;

import com.miu.alumnimanagementportal.funding.enums.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DonationRequest {
    @NotNull
    private Long campaignId;
    @NotNull
    @DecimalMin("1")
    private BigDecimal amount;
    @NotNull
    private PaymentMethod method;

    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
}
