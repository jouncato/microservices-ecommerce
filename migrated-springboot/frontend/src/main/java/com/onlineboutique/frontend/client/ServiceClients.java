package com.onlineboutique.frontend.client;

import com.onlineboutique.common.model.CartItem;
import com.onlineboutique.common.model.Money;
import com.onlineboutique.common.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * HTTP clients for backend services.
 * Migrated from: src/frontend/rpc.go gRPC clients
 */
@Component
public class ServiceClients {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceClients.class);
    
    @Value("${services.product-catalog.url:http://localhost:3550}")
    private String productCatalogUrl;
    
    @Value("${services.currency.url:http://localhost:3551}")
    private String currencyUrl;
    
    @Value("${services.cart.url:http://localhost:3552}")
    private String cartUrl;
    
    @Value("${services.checkout.url:http://localhost:3553}")
    private String checkoutUrl;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    /**
     * Product Catalog Service Client
     */
    public Flux<Product> getProducts() {
        logger.debug("Fetching products from product catalog service");
        return webClientBuilder.build()
                .get()
                .uri(productCatalogUrl + "/api/v1/products")
                .retrieve()
                .bodyToFlux(Product.class);
    }
    
    public Mono<Product> getProduct(String productId) {
        logger.debug("Fetching product {} from product catalog service", productId);
        return webClientBuilder.build()
                .get()
                .uri(productCatalogUrl + "/api/v1/products/{id}", productId)
                .retrieve()
                .bodyToMono(Product.class);
    }
    
    public Flux<Product> searchProducts(String query) {
        logger.debug("Searching products with query: {}", query);
        return webClientBuilder.build()
                .get()
                .uri(productCatalogUrl + "/api/v1/products/search?query={query}", query)
                .retrieve()
                .bodyToFlux(Product.class);
    }
    
    /**
     * Currency Service Client
     */
    public Flux<String> getSupportedCurrencies() {
        logger.debug("Fetching supported currencies");
        return webClientBuilder.build()
                .get()
                .uri(currencyUrl + "/api/v1/currency/supported")
                .retrieve()
                .bodyToFlux(String.class);
    }
    
    public Mono<Money> convertCurrency(Money from, String toCurrency) {
        logger.debug("Converting {} to {}", from, toCurrency);
        ConvertRequest request = new ConvertRequest(from, toCurrency);
        return webClientBuilder.build()
                .post()
                .uri(currencyUrl + "/api/v1/currency/convert")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Money.class);
    }
    
    /**
     * Cart Service Client
     */
    public Flux<CartItem> getCart(String userId) {
        logger.debug("Fetching cart for user {}", userId);
        return webClientBuilder.build()
                .get()
                .uri(cartUrl + "/api/v1/cart/{userId}", userId)
                .retrieve()
                .bodyToFlux(CartItem.class);
    }
    
    public Mono<Void> addToCart(String userId, CartItem item) {
        logger.debug("Adding item {} to cart for user {}", item.getProductId(), userId);
        return webClientBuilder.build()
                .post()
                .uri(cartUrl + "/api/v1/cart/{userId}/items", userId)
                .bodyValue(item)
                .retrieve()
                .bodyToMono(Void.class);
    }
    
    public Mono<Void> emptyCart(String userId) {
        logger.debug("Emptying cart for user {}", userId);
        return webClientBuilder.build()
                .delete()
                .uri(cartUrl + "/api/v1/cart/{userId}", userId)
                .retrieve()
                .bodyToMono(Void.class);
    }
    
    /**
     * Checkout Service Client
     */
    public Mono<OrderResult> placeOrder(PlaceOrderRequest request) {
        logger.debug("Placing order for user {}", request.getUserId());
        return webClientBuilder.build()
                .post()
                .uri(checkoutUrl + "/api/v1/checkout/place-order")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderResult.class);
    }
    
    /**
     * Convert Request DTO
     */
    public static class ConvertRequest {
        private Money from;
        private String toCode;
        
        public ConvertRequest(Money from, String toCode) {
            this.from = from;
            this.toCode = toCode;
        }
        
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
     * Place Order Request DTO
     */
    public static class PlaceOrderRequest {
        private String userId;
        private String userCurrency;
        private String email;
        private String streetAddress;
        private String city;
        private String state;
        private String country;
        private Integer zipCode;
        private String creditCardNumber;
        private Integer creditCardCvv;
        private Integer creditCardExpirationYear;
        private Integer creditCardExpirationMonth;
        
        // Getters and setters
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getUserCurrency() {
            return userCurrency;
        }
        
        public void setUserCurrency(String userCurrency) {
            this.userCurrency = userCurrency;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getStreetAddress() {
            return streetAddress;
        }
        
        public void setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
        }
        
        public String getCity() {
            return city;
        }
        
        public void setCity(String city) {
            this.city = city;
        }
        
        public String getState() {
            return state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
        
        public String getCountry() {
            return country;
        }
        
        public void setCountry(String country) {
            this.country = country;
        }
        
        public Integer getZipCode() {
            return zipCode;
        }
        
        public void setZipCode(Integer zipCode) {
            this.zipCode = zipCode;
        }
        
        public String getCreditCardNumber() {
            return creditCardNumber;
        }
        
        public void setCreditCardNumber(String creditCardNumber) {
            this.creditCardNumber = creditCardNumber;
        }
        
        public Integer getCreditCardCvv() {
            return creditCardCvv;
        }
        
        public void setCreditCardCvv(Integer creditCardCvv) {
            this.creditCardCvv = creditCardCvv;
        }
        
        public Integer getCreditCardExpirationYear() {
            return creditCardExpirationYear;
        }
        
        public void setCreditCardExpirationYear(Integer creditCardExpirationYear) {
            this.creditCardExpirationYear = creditCardExpirationYear;
        }
        
        public Integer getCreditCardExpirationMonth() {
            return creditCardExpirationMonth;
        }
        
        public void setCreditCardExpirationMonth(Integer creditCardExpirationMonth) {
            this.creditCardExpirationMonth = creditCardExpirationMonth;
        }
    }
    
    /**
     * Order Result DTO
     */
    public static class OrderResult {
        private String orderId;
        private String shippingTrackingId;
        private Money shippingCost;
        private String shippingAddress;
        private List<OrderItem> items;
        
        // Getters and setters
        public String getOrderId() {
            return orderId;
        }
        
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        
        public String getShippingTrackingId() {
            return shippingTrackingId;
        }
        
        public void setShippingTrackingId(String shippingTrackingId) {
            this.shippingTrackingId = shippingTrackingId;
        }
        
        public Money getShippingCost() {
            return shippingCost;
        }
        
        public void setShippingCost(Money shippingCost) {
            this.shippingCost = shippingCost;
        }
        
        public String getShippingAddress() {
            return shippingAddress;
        }
        
        public void setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
        }
        
        public List<OrderItem> getItems() {
            return items;
        }
        
        public void setItems(List<OrderItem> items) {
            this.items = items;
        }
    }
    
    /**
     * Order Item DTO
     */
    public static class OrderItem {
        private CartItem item;
        private Money cost;
        
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
    }
}
