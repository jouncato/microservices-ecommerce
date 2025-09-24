package com.onlineboutique.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Order item model.
 * Migrated from: protos/demo.proto OrderItem message
 */
public class OrderItem {
    
    @NotNull(message = "Item is required")
    private CartItem item;
    
    @NotNull(message = "Cost is required")
    private Money cost;

    public OrderItem() {}

    public OrderItem(CartItem item, Money cost) {
        this.item = item;
        this.cost = cost;
    }

    public CartItem getItem() {
        return item;
    }

    public void setItem(CartItem item) {
        this.item = item;
    }

    public Money getCost() {
        return cost;
    }

    public void setCost(Money cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(item, orderItem.item) &&
               Objects.equals(cost, orderItem.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, cost);
    }

    @Override
    public String toString() {
        return String.format("OrderItem{item=%s, cost=%s}", item, cost);
    }
}
