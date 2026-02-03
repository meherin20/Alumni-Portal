package com.miu.alumnimanagementportal.funding.services.impl;

import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.funding.dtos.*;
import com.miu.alumnimanagementportal.funding.entities.Donation;
import com.miu.alumnimanagementportal.funding.entities.FundingCampaign;
import com.miu.alumnimanagementportal.funding.entities.Payment;
import com.miu.alumnimanagementportal.funding.enums.CampaignStatus;
import com.miu.alumnimanagementportal.funding.enums.DonationStatus;
import com.miu.alumnimanagementportal.funding.enums.PaymentMethod;
import com.miu.alumnimanagementportal.funding.repositories.DonationRepository;
import com.miu.alumnimanagementportal.funding.repositories.FundingCampaignRepository;
import com.miu.alumnimanagementportal.funding.repositories.PaymentRepository;
import com.miu.alumnimanagementportal.funding.services.DonationService;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final FundingCampaignRepository campaignRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DonationCreateResultDto createDonation(String userEmail, DonationRequest request) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) throw new BadRequestException("User not found");
        FundingCampaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        if (campaign.getStatus() != CampaignStatus.ACTIVE) throw new BadRequestException("Campaign is not active");
        if (request.getMethod() == PaymentMethod.CARD && !campaign.isCardEnabled())
            throw new BadRequestException("Card payment is not enabled for this campaign");
        if (request.getMethod() == PaymentMethod.BKASH && !campaign.isBkashEnabled())
            throw new BadRequestException("bKash is not enabled for this campaign");

        Donation donation = new Donation();
        donation.setCampaign(campaign);
        donation.setUser(user);
        donation.setAmount(request.getAmount());
        donation.setMethod(request.getMethod());
        donation.setStatus(DonationStatus.PENDING);
        donation = donationRepository.save(donation);

        Payment payment = new Payment();
        payment.setDonation(donation);
        payment.setProvider(request.getMethod() == PaymentMethod.CARD ? "card_gateway" : "bkash");
        payment.setProviderTxnId("TXN-" + donation.getId() + "-" + System.currentTimeMillis());
        payment.setStatus("pending");
        paymentRepository.save(payment);

        DonationCreateResultDto result = new DonationCreateResultDto();
        result.setDonationId(donation.getId());
        result.setCampaignId(campaign.getId());
        result.setAmount(donation.getAmount());
        result.setPaymentUrl("/campaign-detail.html?id=" + campaign.getId() + "&donationSuccess=1&donationId=" + donation.getId());
        result.setMessage("Donation created. For demo, payment is simulated. In production, redirect to payment gateway.");
        return result;
    }

    @Override
    @Transactional
    public List<DonationDto> getMyDonations(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) return Collections.emptyList();
        return donationRepository.findByUserIdOrderByCreatedDateDesc(user.getId()).stream()
                .map(this::toDonationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonationDto getDonation(Long donationId) {
        Donation d = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found"));
        return toDonationDto(d);
    }

    @Override
    @Transactional
    public List<DonationDto> listForAdmin(Long campaignId, String status, String method, String dateFrom, String dateTo) {
        List<Donation> list;
        if (campaignId != null) {
            list = donationRepository.findByCampaignIdOrderByCreatedDateDesc(campaignId);
        } else {
            list = donationRepository.findAll().stream()
                    .sorted(Comparator.comparing(Donation::getCreatedDate).reversed())
                    .collect(Collectors.toList());
        }
        if (status != null && !status.isBlank()) {
            try {
                DonationStatus st = DonationStatus.valueOf(status.toUpperCase());
                list = list.stream().filter(d -> d.getStatus() == st).collect(Collectors.toList());
            } catch (Exception ignored) {}
        }
        if (method != null && !method.isBlank()) {
            try {
                PaymentMethod m = PaymentMethod.valueOf(method.toUpperCase());
                list = list.stream().filter(d -> d.getMethod() == m).collect(Collectors.toList());
            } catch (Exception ignored) {}
        }
        return list.stream().map(this::toDonationDto).collect(Collectors.toList());
    }

    @Override
    public byte[] exportDonationsCsv(Long campaignId, String status, String dateFrom, String dateTo) {
        List<DonationDto> list = listForAdmin(campaignId, status, null, dateFrom, dateTo);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Id,Campaign,User Email,User Name,Amount,Method,Status,Created Date");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (DonationDto d : list) {
            pw.print(d.getId());
            pw.print(",");
            pw.print(escapeCsv(d.getCampaignTitle()));
            pw.print(",");
            pw.print(escapeCsv(d.getUserEmail()));
            pw.print(",");
            pw.print(escapeCsv(d.getUserName()));
            pw.print(",");
            pw.print(d.getAmount());
            pw.print(",");
            pw.print(d.getMethod());
            pw.print(",");
            pw.print(d.getStatus());
            pw.print(",");
            pw.println(d.getCreatedDate() != null ? sdf.format(d.getCreatedDate()) : "");
        }
        pw.flush();
        return baos.toByteArray();
    }

    @Override
    @Transactional
    public void markPaidByWebhook(String provider, String providerTxnId, String webhookPayload) {
        if (providerTxnId == null || providerTxnId.isBlank()) return;
        Optional<Payment> existing = paymentRepository.findByProviderTxnId(providerTxnId);
        if (existing.isPresent()) {
            if (existing.get().getDonation().getStatus() == DonationStatus.PAID) return;
        }
        Payment payment = existing.orElse(null);
        if (payment == null) {
            List<Payment> all = paymentRepository.findAll();
            payment = all.stream().filter(p -> providerTxnId.equals(p.getProviderTxnId())).findFirst().orElse(null);
        }
        if (payment != null) {
            payment.setWebhookPayload(webhookPayload);
            payment.setStatus("success");
            payment.getDonation().setStatus(DonationStatus.PAID);
            donationRepository.save(payment.getDonation());
            paymentRepository.save(payment);
        }
    }

    @Override
    @Transactional
    public void simulatePaid(Long donationId) {
        Donation d = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found"));
        d.setStatus(DonationStatus.PAID);
        donationRepository.save(d);
    }

    private DonationDto toDonationDto(Donation d) {
        DonationDto dto = new DonationDto();
        dto.setId(d.getId());
        dto.setCampaignId(d.getCampaign().getId());
        dto.setCampaignTitle(d.getCampaign().getTitle());
        dto.setUserId(d.getUser().getId());
        dto.setUserEmail(d.getUser().getEmail());
        dto.setUserName(d.getUser().getFirstName() + " " + d.getUser().getLastName());
        dto.setAmount(d.getAmount());
        dto.setMethod(d.getMethod());
        dto.setStatus(d.getStatus());
        dto.setCreatedDate(d.getCreatedDate());
        return dto;
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
}
