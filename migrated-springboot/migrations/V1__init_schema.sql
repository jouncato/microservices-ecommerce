-- V1__init_schema.sql
-- Initial database schema creation
-- Migrated from: Original data structures in Redis, JSON files, and gRPC services

-- Products table (migrated from src/productcatalogservice/products.json)
CREATE TABLE products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    picture VARCHAR(500),
    price_usd_units BIGINT NOT NULL,
    price_usd_nanos INTEGER NOT NULL,
    categories JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for product search and filtering
CREATE INDEX idx_products_categories ON products USING GIN (categories);
CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_price ON products (price_usd_units, price_usd_nanos);

-- Currency rates table (migrated from src/currencyservice/data/currency_conversion.json)
CREATE TABLE currency_rates (
    currency_code VARCHAR(3) PRIMARY KEY,
    rate_to_eur DECIMAL(20,8) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cart items table (migrated from src/cartservice Redis structure)
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id)
);

-- Indexes for cart operations
CREATE INDEX idx_cart_items_user_id ON cart_items (user_id);
CREATE INDEX idx_cart_items_product_id ON cart_items (product_id);

-- Orders table (migrated from checkout service requirements)
CREATE TABLE orders (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    user_currency VARCHAR(3) NOT NULL,
    shipping_address JSONB NOT NULL,
    email VARCHAR(255) NOT NULL,
    credit_card_info JSONB NOT NULL,
    total_amount_units BIGINT NOT NULL,
    total_amount_nanos INTEGER NOT NULL,
    shipping_cost_units BIGINT NOT NULL,
    shipping_cost_nanos INTEGER NOT NULL,
    shipping_tracking_id VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order items table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    price_units BIGINT NOT NULL,
    price_nanos INTEGER NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Indexes for order operations
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_created_at ON orders (created_at);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);

-- User sessions table (migrated from frontend session management)
CREATE TABLE user_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255),
    currency VARCHAR(3) DEFAULT 'USD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for session lookups
CREATE INDEX idx_user_sessions_user_id ON user_sessions (user_id);

-- Recommendations table (for recommendation service)
CREATE TABLE recommendations (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    score DECIMAL(5,4) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id)
);

-- Indexes for recommendations
CREATE INDEX idx_recommendations_user_id ON recommendations (user_id);
CREATE INDEX idx_recommendations_score ON recommendations (score DESC);

-- Ads table (for ad service)
CREATE TABLE ads (
    id BIGSERIAL PRIMARY KEY,
    context_key VARCHAR(255) NOT NULL,
    redirect_url VARCHAR(500) NOT NULL,
    text VARCHAR(500) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for ad lookups
CREATE INDEX idx_ads_context_key ON ads (context_key);
CREATE INDEX idx_ads_active ON ads (active);

-- Comments for documentation
COMMENT ON TABLE products IS 'Product catalog migrated from products.json';
COMMENT ON TABLE currency_rates IS 'Currency exchange rates migrated from currency_conversion.json';
COMMENT ON TABLE cart_items IS 'Shopping cart items migrated from Redis cart service';
COMMENT ON TABLE orders IS 'Order records for checkout service';
COMMENT ON TABLE order_items IS 'Individual items within orders';
COMMENT ON TABLE user_sessions IS 'User session management for frontend';
COMMENT ON TABLE recommendations IS 'Product recommendations for users';
COMMENT ON TABLE ads IS 'Contextual advertisements';
