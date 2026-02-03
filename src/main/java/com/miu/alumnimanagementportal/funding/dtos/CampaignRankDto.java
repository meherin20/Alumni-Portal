package com.miu.alumnimanagementportal.funding.dtos;

import java.math.BigDecimal;

public class CampaignRankDto {
    private Long campaignId;
    private String title;
    private BigDecimal raised;
    private BigDecimal goal;
    private Double percent;

    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getRaised() { return raised; }
    public void setRaised(BigDecimal raised) { this.raised = raised; }
    public BigDecimal getGoal() { return goal; }
    public void setGoal(BigDecimal goal) { this.goal = goal; }
    public Double getPercent() { return percent; }
    public void setPercent(Double percent) { this.percent = percent; }
}
