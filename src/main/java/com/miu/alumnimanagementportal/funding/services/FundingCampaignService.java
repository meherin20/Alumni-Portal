package com.miu.alumnimanagementportal.funding.services;

import com.miu.alumnimanagementportal.funding.dtos.*;
import com.miu.alumnimanagementportal.funding.enums.CampaignStatus;

import java.util.List;

public interface FundingCampaignService {

    FundingCampaignDto create(String adminEmail, CampaignCreateRequest request);
    FundingCampaignDto update(String adminEmail, Long campaignId, CampaignCreateRequest request);
    void delete(String adminEmail, Long campaignId);
    void publish(String adminEmail, Long campaignId);
    void unpublish(String adminEmail, Long campaignId);
    void close(String adminEmail, Long campaignId);

    List<FundingCampaignDto> listAllForAdmin(String adminEmail);
    FundingCampaignDto getForAdmin(String adminEmail, Long campaignId);

    List<FundingCampaignDto> listActive(Long eventId);
    FundingCampaignDto getPublic(Long campaignId);
    List<FundingCampaignDto> listActiveByEvent(Long eventId);

    FundingSummaryDto getSummary();
    List<DonationsOverTimeDto> getDonationsOverTime(String groupBy);
    List<DonationsOverTimeDto> getDonationsOverTimeByCampaign(String groupBy, Long campaignId);
    List<CampaignRankDto> getTopCampaigns(int limit);
}
