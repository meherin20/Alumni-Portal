package com.miu.alumnimanagementportal.funding.repositories;

import com.miu.alumnimanagementportal.funding.entities.Donation;
import com.miu.alumnimanagementportal.funding.enums.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByUserIdOrderByCreatedDateDesc(Long userId);

    List<Donation> findByCampaignIdOrderByCreatedDateDesc(Long campaignId);

    List<Donation> findByCampaignIdAndStatusOrderByCreatedDateDesc(Long campaignId, DonationStatus status);

    @Query("select coalesce(sum(d.amount), 0) from Donation d where d.campaign.id = :campaignId and d.status = :status")
    BigDecimal sumAmountByCampaignIdAndStatus(Long campaignId, DonationStatus status);

    @Query("select coalesce(sum(d.amount), 0) from Donation d where d.status = :status")
    BigDecimal sumAmountByStatus(DonationStatus status);

    @Query("select d from Donation d join fetch d.campaign join fetch d.user where d.campaign.id = :campaignId and d.status = :status order by d.createdDate desc")
    List<Donation> findByCampaignIdAndStatusWithUser(Long campaignId, DonationStatus status);
}
