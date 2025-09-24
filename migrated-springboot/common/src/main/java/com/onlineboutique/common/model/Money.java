package com.onlineboutique.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Money model representing currency amounts with precision.
 * Migrated from: src/frontend/money/money.go, src/checkoutservice/money/money.go
 */
public class Money {
    
    @NotBlank(message = "Currency code is required")
    @JsonProperty("currency_code")
    private String currencyCode;
    
    @NotNull(message = "Units are required")
    @PositiveOrZero(message = "Units must be positive or zero")
    private Long units;
    
    @NotNull(message = "Nanos are required")
    private Integer nanos;

    public Money() {}

    public Money(String currencyCode, Long units, Integer nanos) {
        this.currencyCode = currencyCode;
        this.units = units;
        this.nanos = nanos;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getUnits() {
        return units;
    }

    public void setUnits(Long units) {
        this.units = units;
    }

    public Integer getNanos() {
        return nanos;
    }

    public void setNanos(Integer nanos) {
        this.nanos = nanos;
    }

    /**
     * Convert Money to BigDecimal for calculations
     */
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(units).add(BigDecimal.valueOf(nanos, 9));
    }

    /**
     * Create Money from BigDecimal
     */
    public static Money fromBigDecimal(BigDecimal amount, String currencyCode) {
        long units = amount.longValue();
        int nanos = amount.subtract(BigDecimal.valueOf(units))
                .multiply(BigDecimal.valueOf(1_000_000_000))
                .intValue();
        return new Money(currencyCode, units, nanos);
    }

    /**
     * Add two Money amounts (must be same currency)
     */
    public Money add(Money other) {
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        
        long totalUnits = this.units + other.units;
        int totalNanos = this.nanos + other.nanos;
        
        // Handle overflow in nanos
        if (totalNanos >= 1_000_000_000) {
            totalUnits += 1;
            totalNanos -= 1_000_000_000;
        }
        
        return new Money(this.currencyCode, totalUnits, totalNanos);
    }

    /**
     * Multiply Money by a factor
     */
    public Money multiply(BigDecimal factor) {
        BigDecimal result = toBigDecimal().multiply(factor);
        return fromBigDecimal(result, this.currencyCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(currencyCode, money.currencyCode) &&
               Objects.equals(units, money.units) &&
               Objects.equals(nanos, money.nanos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode, units, nanos);
    }

    @Override
    public String toString() {
        return String.format("%s %d.%09d", currencyCode, units, nanos);
    }
}
