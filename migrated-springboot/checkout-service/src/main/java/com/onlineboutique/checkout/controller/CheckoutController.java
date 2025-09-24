package com.onlineboutique.checkout.controller;

import com.onlineboutique.checkout.service.CheckoutService;
import com.onlineboutique.common.model.OrderResult;
import com.onlineboutique.common.model.PlaceOrderRequest;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Checkout REST controller.
 * Migrated from: src/checkoutservice/main.go gRPC endpoints
 */
@RestController
@RequestMapping("/api/v1/checkout")
@Timed("checkout_service")
public class CheckoutController {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    
    @Autowired
    private CheckoutService checkoutService;
    
    /**
     * Place order
     * Migrated from: PlaceOrder gRPC method
     */
    @PostMapping("/place-order")
    public ResponseEntity<OrderResult> placeOrder(@RequestBody PlaceOrderRequest request) {
        logger.info("Processing order for user: {}", request.getUserId());
        OrderResult result = checkoutService.placeOrder(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Checkout Service is healthy");
    }
}
