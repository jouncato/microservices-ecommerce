package com.onlineboutique.currency.service;

import com.onlineboutique.currency.entity.CurrencyRateEntity;
import com.onlineboutique.currency.repository.CurrencyRateRepository;
import com.onlineboutique.common.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CurrencyService.
 * Migrated from: src/currencyservice/server.js test logic
 */
@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    private CurrencyService currencyService;

    private CurrencyRateEntity usdRate;
    private CurrencyRateEntity eurRate;

    @BeforeEach
    void setUp() {
        usdRate = new CurrencyRateEntity();
        usdRate.setCurrencyCode("USD");
        usdRate.setRateToEur(new BigDecimal("1.1305"));

        eurRate = new CurrencyRateEntity();
        eurRate.setCurrencyCode("EUR");
        eurRate.setRateToEur(new BigDecimal("1.0"));
    }

    @Test
    void testGetSupportedCurrencies() {
        // Given
        List<CurrencyRateEntity> rates = Arrays.asList(usdRate, eurRate);
        when(currencyRateRepository.findAllByOrderByCurrencyCode()).thenReturn(rates);

        // When
        List<String> currencies = currencyService.getSupportedCurrencies();

        // Then
        assertNotNull(currencies);
        assertEquals(2, currencies.size());
        assertEquals("EUR", currencies.get(0));
        assertEquals("USD", currencies.get(1));
        verify(currencyRateRepository).findAllByOrderByCurrencyCode();
    }

    @Test
    void testConvertSameCurrency() {
        // Given
        Money usdMoney = new Money("USD", 10L, 0);

        // When
        Money result = currencyService.convert(usdMoney, "USD");

        // Then
        assertEquals("USD", result.getCurrencyCode());
        assertEquals(10L, result.getUnits());
        assertEquals(0, result.getNanos());
    }

    @Test
    void testConvertUsdToEur() {
        // Given
        Money usdMoney = new Money("USD", 10L, 0);
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(usdRate));
        when(currencyRateRepository.findById("EUR")).thenReturn(Optional.of(eurRate));

        // When
        Money result = currencyService.convert(usdMoney, "EUR");

        // Then
        assertEquals("EUR", result.getCurrencyCode());
        // 10 USD * (1/1.1305) * 1.0 = 8.845 EUR approximately
        assertTrue(result.getUnits() >= 8L);
        assertTrue(result.getUnits() <= 9L);
    }

    @Test
    void testConvertEurToUsd() {
        // Given
        Money eurMoney = new Money("EUR", 10L, 0);
        when(currencyRateRepository.findById("EUR")).thenReturn(Optional.of(eurRate));
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(usdRate));

        // When
        Money result = currencyService.convert(eurMoney, "USD");

        // Then
        assertEquals("USD", result.getCurrencyCode());
        // 10 EUR * (1/1.0) * 1.1305 = 11.305 USD
        assertEquals(11L, result.getUnits());
        assertTrue(result.getNanos() > 300000000); // 0.305 * 1e9
    }

    @Test
    void testConvertUnsupportedSourceCurrency() {
        // Given
        Money money = new Money("INVALID", 10L, 0);
        when(currencyRateRepository.findById("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            currencyService.convert(money, "USD");
        });
    }

    @Test
    void testConvertUnsupportedTargetCurrency() {
        // Given
        Money usdMoney = new Money("USD", 10L, 0);
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(usdRate));
        when(currencyRateRepository.findById("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            currencyService.convert(usdMoney, "INVALID");
        });
    }

    @Test
    void testGetExchangeRate() {
        // Given
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(usdRate));
        when(currencyRateRepository.findById("EUR")).thenReturn(Optional.of(eurRate));

        // When
        BigDecimal rate = currencyService.getExchangeRate("USD", "EUR");

        // Then
        assertNotNull(rate);
        // Rate should be approximately 1/1.1305 = 0.8845
        assertTrue(rate.compareTo(new BigDecimal("0.88")) > 0);
        assertTrue(rate.compareTo(new BigDecimal("0.89")) < 0);
    }

    @Test
    void testIsCurrencySupported() {
        // Given
        when(currencyRateRepository.existsById("USD")).thenReturn(true);
        when(currencyRateRepository.existsById("INVALID")).thenReturn(false);

        // When & Then
        assertTrue(currencyService.isCurrencySupported("USD"));
        assertFalse(currencyService.isCurrencySupported("INVALID"));
    }
}
