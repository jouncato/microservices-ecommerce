package com.onlineboutique.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Order result model.
 * Migrated from: protos/demo.proto OrderResult message
 */
public class OrderResult {
    
    @NotBlank(message = "Order ID is required")
    @JsonProperty("order_id")
    private String orderId;
    
    @NotBlank(message = "Shipping tracking ID is required")
    @JsonProperty("shipping_tracking_id")
    private String shippingTrackingId;
    
    @NotNull(message = "Shipping cost is required")
    @JsonProperty("shipping_cost")
    private Money shippingCost;
    
    @NotNull(message = "Shipping address is required")
    @JsonProperty("shipping_address")
    private Address shippingAddress;
    
    @NotNull(message = "Items are required")
    private List<OrderItem> items;

    public OrderResult() {}

    public OrderResult(String orderId, String shippingTrackingId, Money shippingCost, 
                      Address shippingAddress, List<OrderItem> items) {
        this.orderId = orderId;
        this.shippingTrackingId = shippingTrackingId;
        this.shippingCost = shippingCost;
        this.shippingAddress = shippingAddress;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getShippingTrackingId() {
        return shippingTrackingId;
    }

    public void setShippingTrackingId(String shippingTrackingId) {
        this.shippingTrackingId = shippingTrackingId;
    }

    public Money getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Money shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderResult that = (OrderResult) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(shippingTrackingId, that.shippingTrackingId) &&
               Objects.equals(shippingCost, that.shippingCost) &&
               Objects.equals(shippingAddress, that.shippingAddress) &&
               Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, shippingTrackingId, shippingCost, shippingAddress, items);
    }

    @Override
    public String toString() {
        return String.format("OrderResult{orderId='%s', shippingTrackingId='%s', itemsCount=%d}", 
                           orderId, shippingTrackingId, items != null ? items.size() : 0);
    }
}
