package com.miu.alumnimanagementportal.funding.controllers;

import com.miu.alumnimanagementportal.funding.services.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/funding/webhook")
@RequiredArgsConstructor
public class FundingWebhookController {

    private final DonationService donationService;

    @PostMapping("/card")
    public ResponseEntity<?> cardWebhook(@RequestBody Map<String, Object> payload) {
        String txnId = (String) payload.get("transaction_id");
        if (txnId == null) txnId = (String) payload.get("provider_txn_id");
        donationService.markPaidByWebhook("card_gateway", txnId, payload.toString());
        return ResponseEntity.ok(Map.of("status", "received"));
    }

    @PostMapping("/bkash")
    public ResponseEntity<?> bkashWebhook(@RequestBody Map<String, Object> payload) {
        String txnId = (String) payload.get("trxID");
        if (txnId == null) txnId = (String) payload.get("transaction_id");
        if (txnId == null) txnId = (String) payload.get("provider_txn_id");
        donationService.markPaidByWebhook("bkash", txnId, payload.toString());
        return ResponseEntity.ok(Map.of("status", "received"));
    }
}
