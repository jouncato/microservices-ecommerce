package com.onlineboutique.currency.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Currency rate entity for JPA persistence.
 * Migrated from: src/currencyservice/data/currency_conversion.json
 */
@Entity
@Table(name = "currency_rates")
public class CurrencyRateEntity {
    
    @Id
    @Column(name = "currency_code", length = 3)
    private String currencyCode;
    
    @Column(name = "rate_to_eur", precision = 20, scale = 8, nullable = false)
    private BigDecimal rateToEur;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CurrencyRateEntity() {}

    public CurrencyRateEntity(String currencyCode, BigDecimal rateToEur) {
        this.currencyCode = currencyCode;
        this.rateToEur = rateToEur;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getRateToEur() {
        return rateToEur;
    }

    public void setRateToEur(BigDecimal rateToEur) {
        this.rateToEur = rateToEur;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
