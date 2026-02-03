package com.miu.alumnimanagementportal.funding.services;

import com.miu.alumnimanagementportal.funding.dtos.*;

import java.util.List;

public interface DonationService {

    DonationCreateResultDto createDonation(String userEmail, DonationRequest request);
    List<DonationDto> getMyDonations(String userEmail);
    DonationDto getDonation(Long donationId);

    List<DonationDto> listForAdmin(Long campaignId, String status, String method, String dateFrom, String dateTo);
    byte[] exportDonationsCsv(Long campaignId, String status, String dateFrom, String dateTo);

    void markPaidByWebhook(String provider, String providerTxnId, String webhookPayload);

    void simulatePaid(Long donationId);
}
