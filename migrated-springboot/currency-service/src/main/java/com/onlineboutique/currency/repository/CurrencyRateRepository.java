package com.onlineboutique.currency.repository;

import com.onlineboutique.currency.entity.CurrencyRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Currency rate repository for database operations.
 * Migrated from: src/currencyservice/server.js currency data operations
 */
@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, String> {
    
    /**
     * Find all supported currency codes
     */
    List<CurrencyRateEntity> findAllByOrderByCurrencyCode();
}
