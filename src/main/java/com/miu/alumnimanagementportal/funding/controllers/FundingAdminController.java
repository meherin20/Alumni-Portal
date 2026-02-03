package com.miu.alumnimanagementportal.funding.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.funding.dtos.*;
import com.miu.alumnimanagementportal.funding.services.DonationService;
import com.miu.alumnimanagementportal.funding.services.FundingCampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/funding")
@RequiredArgsConstructor
public class FundingAdminController {

    private final FundingCampaignService campaignService;
    private final DonationService donationService;
    private final Converter converter;

    @GetMapping("/campaigns")
    public ResponseEntity<?> listCampaigns(@RequestParam String adminEmail) {
        List<FundingCampaignDto> list = campaignService.listAllForAdmin(adminEmail);
        return converter.buildResponseEntity(Map.of("data", list), HttpStatus.OK);
    }

    @PostMapping("/campaigns")
    public ResponseEntity<?> createCampaign(@RequestParam String adminEmail, @Valid @RequestBody CampaignCreateRequest request) {
        FundingCampaignDto created = campaignService.create(adminEmail, request);
        return converter.buildResponseEntity(Map.of("data", created), HttpStatus.CREATED);
    }

    @PutMapping("/campaigns/{id}")
    public ResponseEntity<?> updateCampaign(@RequestParam String adminEmail, @PathVariable Long id, @Valid @RequestBody CampaignCreateRequest request) {
        FundingCampaignDto updated = campaignService.update(adminEmail, id, request);
        return converter.buildResponseEntity(Map.of("data", updated), HttpStatus.OK);
    }

    @DeleteMapping("/campaigns/{id}")
    public ResponseEntity<?> deleteCampaign(@RequestParam String adminEmail, @PathVariable Long id) {
        campaignService.delete(adminEmail, id);
        return converter.buildResponseEntity(Map.of("message", "Campaign deleted"), HttpStatus.OK);
    }

    @PostMapping("/campaigns/{id}/publish")
    public ResponseEntity<?> publishCampaign(@RequestParam String adminEmail, @PathVariable Long id) {
        campaignService.publish(adminEmail, id);
        return converter.buildResponseEntity(Map.of("message", "Campaign published"), HttpStatus.OK);
    }

    @PostMapping("/campaigns/{id}/unpublish")
    public ResponseEntity<?> unpublishCampaign(@RequestParam String adminEmail, @PathVariable Long id) {
        campaignService.unpublish(adminEmail, id);
        return converter.buildResponseEntity(Map.of("message", "Campaign unpublished"), HttpStatus.OK);
    }

    @PostMapping("/campaigns/{id}/close")
    public ResponseEntity<?> closeCampaign(@RequestParam String adminEmail, @PathVariable Long id) {
        campaignService.close(adminEmail, id);
        return converter.buildResponseEntity(Map.of("message", "Campaign closed"), HttpStatus.OK);
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<?> getCampaign(@RequestParam String adminEmail, @PathVariable Long id) {
        FundingCampaignDto dto = campaignService.getForAdmin(adminEmail, id);
        return converter.buildResponseEntity(Map.of("data", dto), HttpStatus.OK);
    }

    @GetMapping("/donations")
    public ResponseEntity<?> listDonations(
            @RequestParam String adminEmail,
            @RequestParam(required = false) Long campaignId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        List<DonationDto> list = donationService.listForAdmin(campaignId, status, method, dateFrom, dateTo);
        return converter.buildResponseEntity(Map.of("data", list), HttpStatus.OK);
    }

    @GetMapping(value = "/donations/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportDonations(
            @RequestParam String adminEmail,
            @RequestParam(required = false) Long campaignId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        byte[] csv = donationService.exportDonationsCsv(campaignId, status, dateFrom, dateTo);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "donations.csv");
        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> analytics(@RequestParam String adminEmail) {
        FundingSummaryDto summary = campaignService.getSummary();
        List<DonationsOverTimeDto> overTime = campaignService.getDonationsOverTime("day");
        List<CampaignRankDto> top = campaignService.getTopCampaigns(20);
        List<java.util.Map<String, Object>> campaignAnalytics = new java.util.ArrayList<>();
        for (CampaignRankDto c : top) {
            List<DonationsOverTimeDto> campaignOverTime = campaignService.getDonationsOverTimeByCampaign("day", c.getCampaignId());
            campaignAnalytics.add(java.util.Map.<String, Object>of(
                    "campaignId", c.getCampaignId(),
                    "title", c.getTitle() != null ? c.getTitle() : "",
                    "raised", c.getRaised() != null ? c.getRaised() : java.math.BigDecimal.ZERO,
                    "goal", c.getGoal() != null ? c.getGoal() : java.math.BigDecimal.ZERO,
                    "percent", c.getPercent() != null ? c.getPercent() : 0.0,
                    "donationsOverTime", campaignOverTime
            ));
        }
        return converter.buildResponseEntity(Map.of(
                "summary", summary,
                "donationsOverTime", overTime,
                "topCampaigns", top,
                "campaignAnalytics", campaignAnalytics
        ), HttpStatus.OK);
    }
}
