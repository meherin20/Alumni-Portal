package com.miu.alumnimanagementportal.funding.repositories;

import com.miu.alumnimanagementportal.funding.entities.FundingCampaign;
import com.miu.alumnimanagementportal.funding.enums.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FundingCampaignRepository extends JpaRepository<FundingCampaign, Long> {

    List<FundingCampaign> findByStatusOrderByCreatedDateDesc(CampaignStatus status);

    List<FundingCampaign> findByEventIdAndStatusOrderByCreatedDateDesc(Long eventId, CampaignStatus status);

    List<FundingCampaign> findByEventIdOrderByCreatedDateDesc(Long eventId);

    @Query("select c from FundingCampaign c left join fetch c.event where c.status = :status order by c.createdDate desc")
    List<FundingCampaign> findByStatusWithEvent(CampaignStatus status);
}
