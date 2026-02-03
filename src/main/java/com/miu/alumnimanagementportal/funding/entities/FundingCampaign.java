package com.miu.alumnimanagementportal.funding.entities;

import com.miu.alumnimanagementportal.entities.BaseEntity;
import com.miu.alumnimanagementportal.entities.Event;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.funding.enums.CampaignStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "funding_campaign")
@EqualsAndHashCode(callSuper = true)
public class FundingCampaign extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "goal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal goalAmount = BigDecimal.ZERO;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "card_enabled", nullable = false)
    private boolean cardEnabled = true;

    @Column(name = "bkash_enabled", nullable = false)
    private boolean bkashEnabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id")
    private User createdByAdmin;
}
