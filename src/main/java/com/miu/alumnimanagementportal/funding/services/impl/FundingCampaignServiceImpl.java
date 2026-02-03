package com.miu.alumnimanagementportal.funding.services.impl;

import com.miu.alumnimanagementportal.entities.Event;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.funding.dtos.*;
import com.miu.alumnimanagementportal.funding.entities.Donation;
import com.miu.alumnimanagementportal.funding.entities.FundingCampaign;
import com.miu.alumnimanagementportal.funding.enums.CampaignStatus;
import com.miu.alumnimanagementportal.funding.enums.DonationStatus;
import com.miu.alumnimanagementportal.funding.repositories.DonationRepository;
import com.miu.alumnimanagementportal.funding.repositories.FundingCampaignRepository;
import com.miu.alumnimanagementportal.funding.services.FundingCampaignService;
import com.miu.alumnimanagementportal.repositories.EventRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundingCampaignServiceImpl implements FundingCampaignService {

    private final FundingCampaignRepository campaignRepository;
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public FundingCampaignDto create(String adminEmail, CampaignCreateRequest request) {
        User admin = requireAdmin(adminEmail);
        FundingCampaign c = new FundingCampaign();
        mapRequestToEntity(request, c);
        c.setStatus(CampaignStatus.DRAFT);
        c.setCreatedByAdmin(admin);
        c = campaignRepository.save(c);
        return toDto(c);
    }

    @Override
    @Transactional
    public FundingCampaignDto update(String adminEmail, Long campaignId, CampaignCreateRequest request) {
        requireAdmin(adminEmail);
        FundingCampaign c = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        mapRequestToEntity(request, c);
        c = campaignRepository.save(c);
        return toDto(c);
    }

    @Override
    @Transactional
    public void delete(String adminEmail, Long campaignId) {
        requireAdmin(adminEmail);
        FundingCampaign c = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        campaignRepository.delete(c);
    }

    @Override
    @Transactional
    public void publish(String adminEmail, Long campaignId) {
        requireAdmin(adminEmail);
        FundingCampaign c = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        c.setStatus(CampaignStatus.ACTIVE);
        campaignRepository.save(c);
    }

    @Override
    @Transactional
    public void unpublish(String adminEmail, Long campaignId) {
        requireAdmin(adminEmail);
        FundingCampaign c = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        c.setStatus(CampaignStatus.DRAFT);
        campaignRepository.save(c);
    }

    @Override
    @Transactional
    public void close(String adminEmail, Long campaignId) {
        requireAdmin(adminEmail);
        FundingCampaign c = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        c.setStatus(CampaignStatus.CLOSED);
        campaignRepository.save(c);
    }

    @Override
    @Transactional
    public List<FundingCampaignDto> listAllForAdmin(String adminEmail) {
        requireAdmin(adminEmail);
        return campaignRepository.findAll().stream()
                .sorted(Comparator.comparing(FundingCampaign::getCreatedDate).reversed())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FundingCampaignDto getForAdmin(String adminEmail, Long campaignId) {
        requireAdmin(adminEmail);
        FundingCampaign c = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        return toDto(c);
    }

    @Override
    @Transactional
    public List<FundingCampaignDto> listActive(Long eventId) {
        List<FundingCampaign> list = eventId != null
                ? campaignRepository.findByEventIdAndStatusOrderByCreatedDateDesc(eventId, CampaignStatus.ACTIVE)
                : campaignRepository.findByStatusOrderByCreatedDateDesc(CampaignStatus.ACTIVE);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FundingCampaignDto getPublic(Long campaignId) {
        FundingCampaign c = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        if (c.getStatus() != CampaignStatus.ACTIVE && c.getStatus() != CampaignStatus.CLOSED) {
            throw new ResourceNotFoundException("Campaign not found");
        }
        return toDto(c);
    }

    @Override
    @Transactional
    public List<FundingCampaignDto> listActiveByEvent(Long eventId) {
        return campaignRepository.findByEventIdAndStatusOrderByCreatedDateDesc(eventId, CampaignStatus.ACTIVE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FundingSummaryDto getSummary() {
        List<FundingCampaign> active = campaignRepository.findByStatusOrderByCreatedDateDesc(CampaignStatus.ACTIVE);
        BigDecimal totalGoal = active.stream().map(FundingCampaign::getGoalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRaised = donationRepository.sumAmountByStatus(DonationStatus.PAID);
        if (totalRaised == null) totalRaised = BigDecimal.ZERO;
        double percent = totalGoal.compareTo(BigDecimal.ZERO) == 0 ? 0
                : totalRaised.multiply(BigDecimal.valueOf(100)).divide(totalGoal, 2, RoundingMode.HALF_UP).doubleValue();

        FundingSummaryDto dto = new FundingSummaryDto();
        dto.setTotalGoal(totalGoal);
        dto.setTotalRaised(totalRaised);
        dto.setOverallPercent(Math.min(100.0, percent));
        dto.setFeaturedCampaigns(active.stream().limit(6).map(this::toDto).collect(Collectors.toList()));
        dto.setDonationsOverTime(getDonationsOverTime("day"));
        dto.setTopCampaigns(getTopCampaigns(10));
        return dto;
    }

    @Override
    @Transactional
    public List<DonationsOverTimeDto> getDonationsOverTime(String groupBy) {
        List<Donation> paid = donationRepository.findAll().stream()
                .filter(d -> d.getStatus() == DonationStatus.PAID)
                .collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, DonationsOverTimeDto> byDate = new LinkedHashMap<>();
        for (Donation d : paid) {
            String key = sdf.format(d.getCreatedDate());
            byDate.computeIfAbsent(key, k -> {
                DonationsOverTimeDto x = new DonationsOverTimeDto();
                x.setDate(k);
                x.setAmount(BigDecimal.ZERO);
                x.setCount(0L);
                return x;
            });
            DonationsOverTimeDto x = byDate.get(key);
            x.setAmount(x.getAmount().add(d.getAmount()));
            x.setCount(x.getCount() + 1);
        }
        return new ArrayList<>(byDate.values());
    }

    @Override
    @Transactional
    public List<DonationsOverTimeDto> getDonationsOverTimeByCampaign(String groupBy, Long campaignId) {
        if (campaignId == null) return getDonationsOverTime(groupBy);
        List<Donation> paid = donationRepository.findByCampaignIdAndStatusOrderByCreatedDateDesc(campaignId, DonationStatus.PAID);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, DonationsOverTimeDto> byDate = new LinkedHashMap<>();
        for (Donation d : paid) {
            String key = sdf.format(d.getCreatedDate());
            byDate.computeIfAbsent(key, k -> {
                DonationsOverTimeDto x = new DonationsOverTimeDto();
                x.setDate(k);
                x.setAmount(BigDecimal.ZERO);
                x.setCount(0L);
                return x;
            });
            DonationsOverTimeDto x = byDate.get(key);
            x.setAmount(x.getAmount().add(d.getAmount()));
            x.setCount(x.getCount() + 1);
        }
        return new ArrayList<>(byDate.values());
    }

    @Override
    @Transactional
    public List<CampaignRankDto> getTopCampaigns(int limit) {
        List<FundingCampaign> all = campaignRepository.findByStatusWithEvent(CampaignStatus.ACTIVE);
        all.addAll(campaignRepository.findByStatusWithEvent(CampaignStatus.CLOSED));
        List<CampaignRankDto> ranks = new ArrayList<>();
        for (FundingCampaign c : all) {
            BigDecimal raised = donationRepository.sumAmountByCampaignIdAndStatus(c.getId(), DonationStatus.PAID);
            if (raised == null) raised = BigDecimal.ZERO;
            CampaignRankDto r = new CampaignRankDto();
            r.setCampaignId(c.getId());
            r.setTitle(c.getTitle());
            r.setRaised(raised);
            r.setGoal(c.getGoalAmount());
            r.setPercent(c.getGoalAmount().compareTo(BigDecimal.ZERO) == 0 ? 0.0
                    : raised.multiply(BigDecimal.valueOf(100)).divide(c.getGoalAmount(), 2, RoundingMode.HALF_UP).doubleValue());
            ranks.add(r);
        }
        return ranks.stream()
                .sorted(Comparator.comparing(CampaignRankDto::getRaised).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private User requireAdmin(String email) {
        if (email == null || email.isBlank()) throw new BadRequestException("Admin email is required");
        User u = userRepository.findByEmail(email.trim());
        if (u == null || u.getRole() == null || !"ADMIN".equalsIgnoreCase(u.getRole().getTitle()))
            throw new BadRequestException("Admin not found");
        return u;
    }

    private void mapRequestToEntity(CampaignCreateRequest req, FundingCampaign c) {
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setGoalAmount(req.getGoalAmount());
        c.setStartDate(req.getStartDate());
        c.setEndDate(req.getEndDate());
        c.setContactPhone(req.getContactPhone());
        c.setCardEnabled(req.isCardEnabled());
        c.setBkashEnabled(req.isBkashEnabled());
        if (req.getEventId() != null) {
            c.setEvent(eventRepository.findById(req.getEventId()).orElse(null));
        } else {
            c.setEvent(null);
        }
    }

    private FundingCampaignDto toDto(FundingCampaign c) {
        FundingCampaignDto dto = new FundingCampaignDto();
        dto.setId(c.getId());
        dto.setEventId(c.getEvent() != null ? c.getEvent().getId() : null);
        dto.setEventTitle(c.getEvent() != null ? c.getEvent().getTitle() : null);
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setGoalAmount(c.getGoalAmount());
        dto.setStartDate(c.getStartDate());
        dto.setEndDate(c.getEndDate());
        dto.setStatus(c.getStatus());
        dto.setContactPhone(c.getContactPhone());
        dto.setCardEnabled(c.isCardEnabled());
        dto.setBkashEnabled(c.isBkashEnabled());
        BigDecimal raised = donationRepository.sumAmountByCampaignIdAndStatus(c.getId(), DonationStatus.PAID);
        dto.setRaisedAmount(raised != null ? raised : BigDecimal.ZERO);
        if (c.getGoalAmount() != null && c.getGoalAmount().compareTo(BigDecimal.ZERO) > 0 && dto.getRaisedAmount() != null) {
            dto.setPercentRaised(dto.getRaisedAmount().multiply(BigDecimal.valueOf(100)).divide(c.getGoalAmount(), 2, RoundingMode.HALF_UP).doubleValue());
        } else {
            dto.setPercentRaised(0.0);
        }
        return dto;
    }
}
