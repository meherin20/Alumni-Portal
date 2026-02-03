package com.miu.alumnimanagementportal.funding.entities;

import com.miu.alumnimanagementportal.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "payment")
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @Column(nullable = false, length = 50)
    private String provider; // card_gateway, bkash

    @Column(name = "provider_txn_id", length = 255)
    private String providerTxnId;

    @Column(length = 50)
    private String status;

    @Column(name = "webhook_payload", columnDefinition = "TEXT")
    private String webhookPayload;
}
