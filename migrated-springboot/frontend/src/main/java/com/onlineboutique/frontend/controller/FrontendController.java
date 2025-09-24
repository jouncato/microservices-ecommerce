package com.onlineboutique.frontend.controller;

import com.onlineboutique.frontend.client.ServiceClients;
import com.onlineboutique.common.model.CartItem;
import com.onlineboutique.common.model.Money;
import com.onlineboutique.common.model.Product;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Frontend web controller.
 * Migrated from: src/frontend/handlers.go HTTP handlers
 */
@Controller
@Timed("frontend_service")
public class FrontendController {
    
    private static final Logger logger = LoggerFactory.getLogger(FrontendController.class);
    
    @Autowired
    private ServiceClients serviceClients;
    
    /**
     * Home page handler
     * Migrated from: homeHandler
     */
    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "USD") String currency, 
                      @SessionAttribute(value = "session_id", required = false) String sessionId,
                      Model model) {
        logger.info("Rendering home page with currency: {}", currency);
        
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            model.addAttribute("session_id", sessionId);
        }
        
        // Fetch products
        List<Product> products = serviceClients.getProducts().collectList().block();
        model.addAttribute("products", products);
        
        // Fetch supported currencies
        List<String> currencies = serviceClients.getSupportedCurrencies().collectList().block();
        model.addAttribute("currencies", currencies);
        model.addAttribute("currentCurrency", currency);
        
        // Fetch cart
        List<CartItem> cart = serviceClients.getCart(sessionId).collectList().block();
        model.addAttribute("cart", cart);
        
        return "home";
    }
    
    /**
     * Product detail page handler
     * Migrated from: productHandler
     */
    @GetMapping("/product/{id}")
    public String product(@PathVariable String id, 
                         @RequestParam(defaultValue = "USD") String currency,
                         @SessionAttribute(value = "session_id", required = false) String sessionId,
                         Model model) {
        logger.info("Rendering product page for product: {} with currency: {}", id, currency);
        
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            model.addAttribute("session_id", sessionId);
        }
        
        // Fetch product
        Product product = serviceClients.getProduct(id).block();
        if (product == null) {
            return "error";
        }
        
        // Convert price to requested currency
        Money convertedPrice = serviceClients.convertCurrency(product.getPriceUsd(), currency).block();
        product.setPriceUsd(convertedPrice);
        
        model.addAttribute("product", product);
        model.addAttribute("currentCurrency", currency);
        
        return "product";
    }
    
    /**
     * Cart page handler
     * Migrated from: viewCartHandler
     */
    @GetMapping("/cart")
    public String cart(@SessionAttribute(value = "session_id", required = false) String sessionId,
                      @RequestParam(defaultValue = "USD") String currency,
                      Model model) {
        logger.info("Rendering cart page for session: {} with currency: {}", sessionId, currency);
        
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            model.addAttribute("session_id", sessionId);
        }
        
        // Fetch cart
        List<CartItem> cart = serviceClients.getCart(sessionId).collectList().block();
        model.addAttribute("cart", cart);
        model.addAttribute("currentCurrency", currency);
        
        return "cart";
    }
    
    /**
     * Add to cart handler
     * Migrated from: addToCartHandler
     */
    @PostMapping("/cart")
    public String addToCart(@RequestParam String productId,
                           @RequestParam Integer quantity,
                           @SessionAttribute(value = "session_id", required = false) String sessionId,
                           Model model) {
        logger.info("Adding product {} with quantity {} to cart for session: {}", 
                   productId, quantity, sessionId);
        
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            model.addAttribute("session_id", sessionId);
        }
        
        CartItem item = new CartItem(productId, quantity);
        serviceClients.addToCart(sessionId, item).block();
        
        return "redirect:/cart";
    }
    
    /**
     * Empty cart handler
     * Migrated from: emptyCartHandler
     */
    @PostMapping("/cart/empty")
    public String emptyCart(@SessionAttribute(value = "session_id", required = false) String sessionId,
                           Model model) {
        logger.info("Emptying cart for session: {}", sessionId);
        
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            model.addAttribute("session_id", sessionId);
        }
        
        serviceClients.emptyCart(sessionId).block();
        
        return "redirect:/cart";
    }
    
    /**
     * Set currency handler
     * Migrated from: setCurrencyHandler
     */
    @PostMapping("/setCurrency")
    public String setCurrency(@RequestParam String currency) {
        logger.info("Setting currency to: {}", currency);
        return "redirect:/?currency=" + currency;
    }
    
    /**
     * Logout handler
     * Migrated from: logoutHandler
     */
    @GetMapping("/logout")
    public String logout() {
        logger.info("User logout");
        return "redirect:/";
    }
    
    /**
     * Checkout handler
     * Migrated from: placeOrderHandler
     */
    @PostMapping("/cart/checkout")
    public String checkout(@RequestParam String email,
                          @RequestParam String streetAddress,
                          @RequestParam String city,
                          @RequestParam String state,
                          @RequestParam String country,
                          @RequestParam Integer zipCode,
                          @RequestParam String creditCardNumber,
                          @RequestParam Integer creditCardCvv,
                          @RequestParam Integer creditCardExpirationYear,
                          @RequestParam Integer creditCardExpirationMonth,
                          @RequestParam(defaultValue = "USD") String currency,
                          @SessionAttribute(value = "session_id", required = false) String sessionId,
                          Model model) {
        logger.info("Processing checkout for session: {}", sessionId);
        
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            model.addAttribute("session_id", sessionId);
        }
        
        ServiceClients.PlaceOrderRequest request = new ServiceClients.PlaceOrderRequest();
        request.setUserId(sessionId);
        request.setUserCurrency(currency);
        request.setEmail(email);
        request.setStreetAddress(streetAddress);
        request.setCity(city);
        request.setState(state);
        request.setCountry(country);
        request.setZipCode(zipCode);
        request.setCreditCardNumber(creditCardNumber);
        request.setCreditCardCvv(creditCardCvv);
        request.setCreditCardExpirationYear(creditCardExpirationYear);
        request.setCreditCardExpirationMonth(creditCardExpirationMonth);
        
        ServiceClients.OrderResult result = serviceClients.placeOrder(request).block();
        model.addAttribute("orderResult", result);
        
        return "checkout-success";
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "Frontend Service is healthy";
    }
    
    /**
     * Robots.txt handler
     */
    @GetMapping("/robots.txt")
    @ResponseBody
    public String robots() {
        return "User-agent: *\nDisallow: /";
    }
}
