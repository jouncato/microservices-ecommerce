package com.onlineboutique.currency.controller;

import com.onlineboutique.currency.service.CurrencyService;
import com.onlineboutique.common.model.Money;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Currency REST controller.
 * Migrated from: src/currencyservice/server.js gRPC endpoints
 */
@RestController
@RequestMapping("/api/v1/currency")
@Timed("currency_service")
public class CurrencyController {
    
    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);
    
    @Autowired
    private CurrencyService currencyService;
    
    /**
     * Get supported currencies
     * Migrated from: GetSupportedCurrencies gRPC method
     */
    @GetMapping("/supported")
    public ResponseEntity<List<String>> getSupportedCurrencies() {
        logger.info("Getting supported currencies");
        List<String> currencies = currencyService.getSupportedCurrencies();
        return ResponseEntity.ok(currencies);
    }
    
    /**
     * Convert money between currencies
     * Migrated from: Convert gRPC method
     */
    @PostMapping("/convert")
    public ResponseEntity<Money> convert(@RequestBody ConvertRequest request) {
        logger.info("Converting {} to {}", request.getFrom(), request.getToCode());
        Money converted = currencyService.convert(request.getFrom(), request.getToCode());
        return ResponseEntity.ok(converted);
    }
    
    /**
     * Get exchange rate between currencies
     */
    @GetMapping("/rate")
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to) {
        logger.info("Getting exchange rate from {} to {}", from, to);
        BigDecimal rate = currencyService.getExchangeRate(from, to);
        return ResponseEntity.ok(new ExchangeRateResponse(from, to, rate));
    }
    
    /**
     * Check if currency is supported
     */
    @GetMapping("/supported/{currency}")
    public ResponseEntity<Boolean> isCurrencySupported(@PathVariable String currency) {
        logger.info("Checking if currency {} is supported", currency);
        boolean supported = currencyService.isCurrencySupported(currency);
        return ResponseEntity.ok(supported);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Currency Service is healthy");
    }
    
    /**
     * Convert request DTO
     */
    public static class ConvertRequest {
        private Money from;
        private String toCode;
        
        public Money getFrom() {
            return from;
        }
        
        public void setFrom(Money from) {
            this.from = from;
        }
        
        public String getToCode() {
            return toCode;
        }
        
        public void setToCode(String toCode) {
            this.toCode = toCode;
        }
    }
    
    /**
     * Exchange rate response DTO
     */
    public static class ExchangeRateResponse {
        private String from;
        private String to;
        private BigDecimal rate;
        
        public ExchangeRateResponse(String from, String to, BigDecimal rate) {
            this.from = from;
            this.to = to;
            this.rate = rate;
        }
        
        public String getFrom() {
            return from;
        }
        
        public String getTo() {
            return to;
        }
        
        public BigDecimal getRate() {
            return rate;
        }
    }
}
