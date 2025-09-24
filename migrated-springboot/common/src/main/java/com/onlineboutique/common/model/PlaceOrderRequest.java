package com.onlineboutique.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Place order request model.
 * Migrated from: protos/demo.proto PlaceOrderRequest message
 */
public class PlaceOrderRequest {
    
    @NotBlank(message = "User ID is required")
    @JsonProperty("user_id")
    private String userId;
    
    @NotBlank(message = "User currency is required")
    @JsonProperty("user_currency")
    private String userCurrency;
    
    @NotNull(message = "Address is required")
    private Address address;
    
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotNull(message = "Credit card info is required")
    @JsonProperty("credit_card")
    private CreditCardInfo creditCard;

    public PlaceOrderRequest() {}

    public PlaceOrderRequest(String userId, String userCurrency, Address address, String email, CreditCardInfo creditCard) {
        this.userId = userId;
        this.userCurrency = userCurrency;
        this.address = address;
        this.email = email;
        this.creditCard = creditCard;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCurrency() {
        return userCurrency;
    }

    public void setUserCurrency(String userCurrency) {
        this.userCurrency = userCurrency;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CreditCardInfo getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardInfo creditCard) {
        this.creditCard = creditCard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceOrderRequest that = (PlaceOrderRequest) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(userCurrency, that.userCurrency) &&
               Objects.equals(address, that.address) &&
               Objects.equals(email, that.email) &&
               Objects.equals(creditCard, that.creditCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userCurrency, address, email, creditCard);
    }

    @Override
    public String toString() {
        return String.format("PlaceOrderRequest{userId='%s', userCurrency='%s', email='%s'}", 
                           userId, userCurrency, email);
    }
}
