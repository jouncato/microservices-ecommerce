package com.onlineboutique.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

/**
 * Credit card information model.
 * Migrated from: protos/demo.proto CreditCardInfo message
 */
public class CreditCardInfo {
    
    @NotBlank(message = "Credit card number is required")
    @JsonProperty("credit_card_number")
    private String creditCardNumber;
    
    @NotNull(message = "CVV is required")
    @JsonProperty("credit_card_cvv")
    private Integer creditCardCvv;
    
    @NotNull(message = "Expiration year is required")
    @JsonProperty("credit_card_expiration_year")
    private Integer creditCardExpirationYear;
    
    @NotNull(message = "Expiration month is required")
    @JsonProperty("credit_card_expiration_month")
    private Integer creditCardExpirationMonth;

    public CreditCardInfo() {}

    public CreditCardInfo(String creditCardNumber, Integer creditCardCvv, 
                         Integer creditCardExpirationYear, Integer creditCardExpirationMonth) {
        this.creditCardNumber = creditCardNumber;
        this.creditCardCvv = creditCardCvv;
        this.creditCardExpirationYear = creditCardExpirationYear;
        this.creditCardExpirationMonth = creditCardExpirationMonth;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public Integer getCreditCardCvv() {
        return creditCardCvv;
    }

    public void setCreditCardCvv(Integer creditCardCvv) {
        this.creditCardCvv = creditCardCvv;
    }

    public Integer getCreditCardExpirationYear() {
        return creditCardExpirationYear;
    }

    public void setCreditCardExpirationYear(Integer creditCardExpirationYear) {
        this.creditCardExpirationYear = creditCardExpirationYear;
    }

    public Integer getCreditCardExpirationMonth() {
        return creditCardExpirationMonth;
    }

    public void setCreditCardExpirationMonth(Integer creditCardExpirationMonth) {
        this.creditCardExpirationMonth = creditCardExpirationMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditCardInfo that = (CreditCardInfo) o;
        return Objects.equals(creditCardNumber, that.creditCardNumber) &&
               Objects.equals(creditCardCvv, that.creditCardCvv) &&
               Objects.equals(creditCardExpirationYear, that.creditCardExpirationYear) &&
               Objects.equals(creditCardExpirationMonth, that.creditCardExpirationMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creditCardNumber, creditCardCvv, creditCardExpirationYear, creditCardExpirationMonth);
    }

    @Override
    public String toString() {
        return String.format("CreditCardInfo{number='%s', cvv=%d, expires=%d/%d}", 
                           creditCardNumber, creditCardCvv, creditCardExpirationMonth, creditCardExpirationYear);
    }
}
