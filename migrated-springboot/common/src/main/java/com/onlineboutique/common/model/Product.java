package com.onlineboutique.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

/**
 * Product model representing items in the catalog.
 * Migrated from: src/productcatalogservice/products.json structure
 */
public class Product {
    
    @NotBlank(message = "Product ID is required")
    private String id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    private String picture;
    
    @NotNull(message = "Price is required")
    @JsonProperty("price_usd")
    private Money priceUsd;
    
    private String[] categories;

    public Product() {}

    public Product(String id, String name, String description, String picture, Money priceUsd, String[] categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.priceUsd = priceUsd;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Money getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(Money priceUsd) {
        this.priceUsd = priceUsd;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', priceUsd=%s}", id, name, priceUsd);
    }
}
