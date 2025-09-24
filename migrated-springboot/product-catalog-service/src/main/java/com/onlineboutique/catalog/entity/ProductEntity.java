package com.onlineboutique.catalog.entity;

import com.onlineboutique.common.model.Money;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Product entity for JPA persistence.
 * Migrated from: src/productcatalogservice/products.json structure
 */
@Entity
@Table(name = "products")
public class ProductEntity {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "picture", length = 500)
    private String picture;
    
    @Column(name = "price_usd_units", nullable = false)
    private Long priceUsdUnits;
    
    @Column(name = "price_usd_nanos", nullable = false)
    private Integer priceUsdNanos;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "categories", columnDefinition = "jsonb")
    private List<String> categories;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProductEntity() {}

    public ProductEntity(String id, String name, String description, String picture, 
                        Long priceUsdUnits, Integer priceUsdNanos, List<String> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.priceUsdUnits = priceUsdUnits;
        this.priceUsdNanos = priceUsdNanos;
        this.categories = categories;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public Long getPriceUsdUnits() {
        return priceUsdUnits;
    }

    public void setPriceUsdUnits(Long priceUsdUnits) {
        this.priceUsdUnits = priceUsdUnits;
    }

    public Integer getPriceUsdNanos() {
        return priceUsdNanos;
    }

    public void setPriceUsdNanos(Integer priceUsdNanos) {
        this.priceUsdNanos = priceUsdNanos;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Convert to Money object
     */
    public Money getPriceUsd() {
        return new Money("USD", priceUsdUnits, priceUsdNanos);
    }

    /**
     * Set from Money object
     */
    public void setPriceUsd(Money priceUsd) {
        this.priceUsdUnits = priceUsd.getUnits();
        this.priceUsdNanos = priceUsd.getNanos();
    }
}
