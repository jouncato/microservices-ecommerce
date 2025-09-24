package com.onlineboutique.currency.service;

import com.onlineboutique.currency.entity.CurrencyRateEntity;
import com.onlineboutique.currency.repository.CurrencyRateRepository;
import com.onlineboutique.common.model.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Currency service for conversion operations.
 * Migrated from: src/currencyservice/server.js conversion logic
 */
@Service
@Transactional
public class CurrencyService {
    
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    
    @Autowired
    private CurrencyRateRepository currencyRateRepository;
    
    /**
     * Get all supported currency codes
     * Migrated from: GetSupportedCurrencies gRPC method
     */
    public List<String> getSupportedCurrencies() {
        logger.debug("Getting supported currencies");
        List<CurrencyRateEntity> rates = currencyRateRepository.findAllByOrderByCurrencyCode();
        return rates.stream()
                .map(CurrencyRateEntity::getCurrencyCode)
                .toList();
    }
    
    /**
     * Convert money from one currency to another
     * Migrated from: Convert gRPC method
     */
    public Money convert(Money from, String toCurrencyCode) {
        logger.debug("Converting {} to {}", from, toCurrencyCode);
        
        if (from.getCurrencyCode().equals(toCurrencyCode)) {
            return from;
        }
        
        // Get rate from source currency to EUR
        Optional<CurrencyRateEntity> fromRate = currencyRateRepository.findById(from.getCurrencyCode());
        if (fromRate.isEmpty()) {
            throw new IllegalArgumentException("Unsupported source currency: " + from.getCurrencyCode());
        }
        
        // Get rate from target currency to EUR
        Optional<CurrencyRateEntity> toRate = currencyRateRepository.findById(toCurrencyCode);
        if (toRate.isEmpty()) {
            throw new IllegalArgumentException("Unsupported target currency: " + toCurrencyCode);
        }
        
        // Convert: source -> EUR -> target
        BigDecimal fromAmount = from.toBigDecimal();
        BigDecimal eurAmount = fromAmount.divide(fromRate.get().getRateToEur(), 9, RoundingMode.HALF_UP);
        BigDecimal targetAmount = eurAmount.multiply(toRate.get().getRateToEur());
        
        return Money.fromBigDecimal(targetAmount, toCurrencyCode);
    }
    
    /**
     * Get exchange rate between two currencies
     */
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        logger.debug("Getting exchange rate from {} to {}", fromCurrency, toCurrency);
        
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        
        Optional<CurrencyRateEntity> fromRate = currencyRateRepository.findById(fromCurrency);
        if (fromRate.isEmpty()) {
            throw new IllegalArgumentException("Unsupported source currency: " + fromCurrency);
        }
        
        Optional<CurrencyRateEntity> toRate = currencyRateRepository.findById(toCurrency);
        if (toRate.isEmpty()) {
            throw new IllegalArgumentException("Unsupported target currency: " + toCurrency);
        }
        
        // Calculate rate: (1 / fromRate) * toRate
        return BigDecimal.ONE
                .divide(fromRate.get().getRateToEur(), 9, RoundingMode.HALF_UP)
                .multiply(toRate.get().getRateToEur());
    }
    
    /**
     * Check if currency is supported
     */
    public boolean isCurrencySupported(String currencyCode) {
        return currencyRateRepository.existsById(currencyCode);
    }
}
