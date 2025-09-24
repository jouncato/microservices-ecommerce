package com.onlineboutique.checkout.service;

import com.onlineboutique.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Checkout service for order orchestration.
 * Migrated from: src/checkoutservice/main.go checkout logic
 */
@Service
@Transactional
public class CheckoutService {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);
    
    @Value("${services.cart.url:http://localhost:3552}")
    private String cartServiceUrl;
    
    @Value("${services.product-catalog.url:http://localhost:3550}")
    private String productCatalogServiceUrl;
    
    @Value("${services.currency.url:http://localhost:3551}")
    private String currencyServiceUrl;
    
    @Value("${services.payment.url:http://localhost:3554}")
    private String paymentServiceUrl;
    
    @Value("${services.shipping.url:http://localhost:3555}")
    private String shippingServiceUrl;
    
    @Value("${services.email.url:http://localhost:3556}")
    private String emailServiceUrl;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    /**
     * Place order - orchestrate the complete checkout process
     * Migrated from: PlaceOrder gRPC method
     */
    public OrderResult placeOrder(PlaceOrderRequest request) {
        logger.info("Processing order for user: {}", request.getUserId());
        
        try {
            // 1. Get user's cart
            List<CartItem> cartItems = getCartItems(request.getUserId());
            if (cartItems.isEmpty()) {
                throw new IllegalArgumentException("Cart is empty");
            }
            
            // 2. Get product details and calculate totals
            List<OrderItem> orderItems = calculateOrderItems(cartItems, request.getUserCurrency());
            Money totalAmount = calculateTotalAmount(orderItems);
            
            // 3. Process payment
            String transactionId = processPayment(request.getCreditCard(), totalAmount);
            
            // 4. Calculate shipping
            Money shippingCost = calculateShipping(request.getAddress(), cartItems);
            
            // 5. Generate order ID and tracking ID
            String orderId = UUID.randomUUID().toString();
            String trackingId = UUID.randomUUID().toString();
            
            // 6. Send confirmation email
            sendOrderConfirmation(request.getEmail(), orderId, trackingId, shippingCost, 
                                request.getAddress(), orderItems);
            
            // 7. Empty the cart
            emptyCart(request.getUserId());
            
            // 8. Create order result
            OrderResult result = new OrderResult();
            result.setOrderId(orderId);
            result.setShippingTrackingId(trackingId);
            result.setShippingCost(shippingCost);
            result.setShippingAddress(request.getAddress());
            result.setItems(orderItems);
            
            logger.info("Order {} processed successfully for user: {}", orderId, request.getUserId());
            return result;
            
        } catch (Exception e) {
            logger.error("Error processing order for user: {}", request.getUserId(), e);
            throw new RuntimeException("Failed to process order", e);
        }
    }
    
    /**
     * Get cart items from cart service
     */
    private List<CartItem> getCartItems(String userId) {
        logger.debug("Fetching cart items for user: {}", userId);
        return webClientBuilder.build()
                .get()
                .uri(cartServiceUrl + "/api/v1/cart/{userId}", userId)
                .retrieve()
                .bodyToFlux(CartItem.class)
                .collectList()
                .block();
    }
    
    /**
     * Calculate order items with product details and converted prices
     */
    private List<OrderItem> calculateOrderItems(List<CartItem> cartItems, String userCurrency) {
        logger.debug("Calculating order items for {} items in currency: {}", cartItems.size(), userCurrency);
        
        return cartItems.stream()
                .map(cartItem -> {
                    // Get product details
                    Product product = webClientBuilder.build()
                            .get()
                            .uri(productCatalogServiceUrl + "/api/v1/products/{id}", cartItem.getProductId())
                            .retrieve()
                            .bodyToMono(Product.class)
                            .block();
                    
                    if (product == null) {
                        throw new IllegalArgumentException("Product not found: " + cartItem.getProductId());
                    }
                    
                    // Convert price to user currency
                    Money convertedPrice = webClientBuilder.build()
                            .post()
                            .uri(currencyServiceUrl + "/api/v1/currency/convert")
                            .bodyValue(new ConvertRequest(product.getPriceUsd(), userCurrency))
                            .retrieve()
                            .bodyToMono(Money.class)
                            .block();
                    
                    // Calculate total cost for this item
                    Money itemCost = convertedPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                    
                    OrderItem orderItem = new OrderItem();
                    orderItem.setItem(cartItem);
                    orderItem.setCost(itemCost);
                    
                    return orderItem;
                })
                .toList();
    }
    
    /**
     * Calculate total amount for all order items
     */
    private Money calculateTotalAmount(List<OrderItem> orderItems) {
        logger.debug("Calculating total amount for {} order items", orderItems.size());
        
        Money total = new Money("USD", 0L, 0);
        for (OrderItem item : orderItems) {
            total = total.add(item.getCost());
        }
        
        return total;
    }
    
    /**
     * Process payment with payment service
     */
    private String processPayment(CreditCardInfo creditCard, Money amount) {
        logger.debug("Processing payment for amount: {}", amount);
        
        ChargeRequest chargeRequest = new ChargeRequest();
        chargeRequest.setAmount(amount);
        chargeRequest.setCreditCard(creditCard);
        
        ChargeResponse response = webClientBuilder.build()
                .post()
                .uri(paymentServiceUrl + "/api/v1/payment/charge")
                .bodyValue(chargeRequest)
                .retrieve()
                .bodyToMono(ChargeResponse.class)
                .block();
        
        return response.getTransactionId();
    }
    
    /**
     * Calculate shipping cost
     */
    private Money calculateShipping(Address address, List<CartItem> cartItems) {
        logger.debug("Calculating shipping cost for address: {}", address);
        
        ShippingQuoteRequest quoteRequest = new ShippingQuoteRequest();
        quoteRequest.setAddress(address);
        quoteRequest.setItems(cartItems);
        
        ShippingQuoteResponse response = webClientBuilder.build()
                .post()
                .uri(shippingServiceUrl + "/api/v1/shipping/quote")
                .bodyValue(quoteRequest)
                .retrieve()
                .bodyToMono(ShippingQuoteResponse.class)
                .block();
        
        return response.getCostUsd();
    }
    
    /**
     * Send order confirmation email
     */
    private void sendOrderConfirmation(String email, String orderId, String trackingId, 
                                     Money shippingCost, Address address, List<OrderItem> items) {
        logger.debug("Sending order confirmation email to: {}", email);
        
        OrderConfirmationRequest confirmationRequest = new OrderConfirmationRequest();
        confirmationRequest.setEmail(email);
        confirmationRequest.setOrderId(orderId);
        confirmationRequest.setShippingTrackingId(trackingId);
        confirmationRequest.setShippingCost(shippingCost);
        confirmationRequest.setShippingAddress(address);
        confirmationRequest.setItems(items);
        
        webClientBuilder.build()
                .post()
                .uri(emailServiceUrl + "/api/v1/email/send-confirmation")
                .bodyValue(confirmationRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
    
    /**
     * Empty user's cart
     */
    private void emptyCart(String userId) {
        logger.debug("Emptying cart for user: {}", userId);
        
        webClientBuilder.build()
                .delete()
                .uri(cartServiceUrl + "/api/v1/cart/{userId}", userId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
    
    // DTOs for service communication
    public static class ConvertRequest {
        private Money from;
        private String toCode;
        
        public ConvertRequest(Money from, String toCode) {
            this.from = from;
            this.toCode = toCode;
        }
        
        public Money getFrom() { return from; }
        public String getToCode() { return toCode; }
    }
    
    public static class ChargeRequest {
        private Money amount;
        private CreditCardInfo creditCard;
        
        public Money getAmount() { return amount; }
        public void setAmount(Money amount) { this.amount = amount; }
        public CreditCardInfo getCreditCard() { return creditCard; }
        public void setCreditCard(CreditCardInfo creditCard) { this.creditCard = creditCard; }
    }
    
    public static class ChargeResponse {
        private String transactionId;
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    }
    
    public static class ShippingQuoteRequest {
        private Address address;
        private List<CartItem> items;
        
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
        public List<CartItem> getItems() { return items; }
        public void setItems(List<CartItem> items) { this.items = items; }
    }
    
    public static class ShippingQuoteResponse {
        private Money costUsd;
        
        public Money getCostUsd() { return costUsd; }
        public void setCostUsd(Money costUsd) { this.costUsd = costUsd; }
    }
    
    public static class OrderConfirmationRequest {
        private String email;
        private String orderId;
        private String shippingTrackingId;
        private Money shippingCost;
        private Address shippingAddress;
        private List<OrderItem> items;
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public String getShippingTrackingId() { return shippingTrackingId; }
        public void setShippingTrackingId(String shippingTrackingId) { this.shippingTrackingId = shippingTrackingId; }
        public Money getShippingCost() { return shippingCost; }
        public void setShippingCost(Money shippingCost) { this.shippingCost = shippingCost; }
        public Address getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
        public List<OrderItem> getItems() { return items; }
        public void setItems(List<OrderItem> items) { this.items = items; }
    }
}
