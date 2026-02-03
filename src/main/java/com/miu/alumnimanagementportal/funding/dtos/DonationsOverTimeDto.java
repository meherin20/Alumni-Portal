package com.miu.alumnimanagementportal.funding.dtos;

import java.math.BigDecimal;

public class DonationsOverTimeDto {
    private String date; // "2025-01-15" or "2025-W02"
    private BigDecimal amount;
    private Long count;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
