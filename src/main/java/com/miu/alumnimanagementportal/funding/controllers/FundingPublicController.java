package com.miu.alumnimanagementportal.funding.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.funding.dtos.*;
import com.miu.alumnimanagementportal.funding.services.DonationService;
import com.miu.alumnimanagementportal.funding.services.FundingCampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funding")
@RequiredArgsConstructor
public class FundingPublicController {

    private final FundingCampaignService campaignService;
    private final DonationService donationService;
    private final Converter converter;

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        FundingSummaryDto summary = campaignService.getSummary();
        return converter.buildResponseEntity(Map.of("data", summary), HttpStatus.OK);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<?> listActive(@RequestParam(required = false) Long eventId) {
        List<FundingCampaignDto> list = campaignService.listActive(eventId);
        return converter.buildResponseEntity(Map.of("data", list), HttpStatus.OK);
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<?> getCampaign(@PathVariable Long id) {
        FundingCampaignDto campaign = campaignService.getPublic(id);
        return converter.buildResponseEntity(Map.of("data", campaign), HttpStatus.OK);
    }

    @GetMapping("/donations/over-time")
    public ResponseEntity<?> donationsOverTime(@RequestParam(defaultValue = "day") String groupBy) {
        List<DonationsOverTimeDto> list = campaignService.getDonationsOverTime(groupBy);
        return converter.buildResponseEntity(Map.of("data", list), HttpStatus.OK);
    }

    @GetMapping("/campaigns/top")
    public ResponseEntity<?> topCampaigns(@RequestParam(defaultValue = "10") int limit) {
        List<CampaignRankDto> list = campaignService.getTopCampaigns(limit);
        return converter.buildResponseEntity(Map.of("data", list), HttpStatus.OK);
    }

    @PostMapping("/donations")
    public ResponseEntity<?> createDonation(@RequestParam String userEmail, @Valid @RequestBody DonationRequest request) {
        DonationCreateResultDto result = donationService.createDonation(userEmail, request);
        return converter.buildResponseEntity(Map.of("data", result), HttpStatus.CREATED);
    }

    @GetMapping("/my-donations")
    public ResponseEntity<?> myDonations(@RequestParam String userEmail) {
        List<DonationDto> list = donationService.getMyDonations(userEmail);
        return converter.buildResponseEntity(Map.of("data", list), HttpStatus.OK);
    }

    @GetMapping("/donations/{id}")
    public ResponseEntity<?> getDonation(@PathVariable Long id) {
        DonationDto dto = donationService.getDonation(id);
        return converter.buildResponseEntity(Map.of("data", dto), HttpStatus.OK);
    }

    @GetMapping("/events/{eventId}/campaigns")
    public ResponseEntity<?> campaignsByEvent(@PathVariable Long eventId) {
        List<FundingCampaignDto> list = campaignService.listActiveByEvent(eventId);
        return converter.buildResponseEntity(Map.of("data", list), HttpStatus.OK);
    }

    @PostMapping("/donations/{donationId}/simulate-paid")
    public ResponseEntity<?> simulatePaid(@PathVariable Long donationId) {
        donationService.simulatePaid(donationId);
        return converter.buildResponseEntity(Map.of("message", "Donation marked as paid"), HttpStatus.OK);
    }
}
