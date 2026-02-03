package com.miu.alumnimanagementportal.funding.dtos;

import java.math.BigDecimal;
import java.util.List;

public class FundingSummaryDto {
    private BigDecimal totalGoal;
    private BigDecimal totalRaised;
    private Double overallPercent;
    private List<FundingCampaignDto> featuredCampaigns;
    private List<DonationsOverTimeDto> donationsOverTime;
    private List<CampaignRankDto> topCampaigns;

    public BigDecimal getTotalGoal() { return totalGoal; }
    public void setTotalGoal(BigDecimal totalGoal) { this.totalGoal = totalGoal; }
    public BigDecimal getTotalRaised() { return totalRaised; }
    public void setTotalRaised(BigDecimal totalRaised) { this.totalRaised = totalRaised; }
    public Double getOverallPercent() { return overallPercent; }
    public void setOverallPercent(Double overallPercent) { this.overallPercent = overallPercent; }
    public List<FundingCampaignDto> getFeaturedCampaigns() { return featuredCampaigns; }
    public void setFeaturedCampaigns(List<FundingCampaignDto> featuredCampaigns) { this.featuredCampaigns = featuredCampaigns; }
    public List<DonationsOverTimeDto> getDonationsOverTime() { return donationsOverTime; }
    public void setDonationsOverTime(List<DonationsOverTimeDto> donationsOverTime) { this.donationsOverTime = donationsOverTime; }
    public List<CampaignRankDto> getTopCampaigns() { return topCampaigns; }
    public void setTopCampaigns(List<CampaignRankDto> topCampaigns) { this.topCampaigns = topCampaigns; }
}
