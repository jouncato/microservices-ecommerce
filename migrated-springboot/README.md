# Online Boutique - Spring Boot Migration

This is the complete migration of the Online Boutique microservices application from its original multi-language implementation to a unified Java + Spring Boot architecture.

## Architecture Overview

The application has been migrated from:
- **Original**: 11 microservices in Go, C#, Node.js, Python, Java
- **Migrated**: Unified Spring Boot microservices with PostgreSQL

## Services

### Core Services
- **Frontend Service** (Port 8080) - Web interface and API gateway
- **Product Catalog Service** (Port 3550) - Product management and search
- **Currency Service** (Port 3551) - Currency conversion
- **Cart Service** (Port 3552) - Shopping cart management
- **Checkout Service** (Port 3553) - Order orchestration

### Additional Services (To be implemented)
- **Payment Service** (Port 3554) - Payment processing
- **Shipping Service** (Port 3555) - Shipping calculations
- **Email Service** (Port 3556) - Email notifications
- **Recommendation Service** (Port 3557) - Product recommendations
- **Ad Service** (Port 3558) - Advertisement service

## Technology Stack

- **Java 17 LTS** - Programming language
- **Spring Boot 3.2.x** - Application framework
- **PostgreSQL v16** - Primary database
- **Spring Data JPA** - Data access layer
- **Flyway** - Database migrations
- **Docker Alpine** - Containerization
- **Micrometer** - Metrics and observability
- **Thymeleaf** - Template engine for frontend

## Project Structure

```
migrated-springboot/
├── common/                          # Shared models and utilities
├── frontend/                        # Web frontend service
├── product-catalog-service/         # Product management
├── currency-service/                # Currency conversion
├── cart-service/                    # Shopping cart
├── checkout-service/                # Order processing
├── payment-service/                 # Payment processing (TODO)
├── shipping-service/                # Shipping calculations (TODO)
├── email-service/                   # Email notifications (TODO)
├── recommendation-service/          # Product recommendations (TODO)
├── ad-service/                      # Advertisement service (TODO)
├── migrations/                      # Database migration scripts
├── docker/                          # Docker configurations
└── docs/                           # Documentation
```

## Quick Start

### Prerequisites
- Java 17 LTS
- Gradle 8.5+
- PostgreSQL v16
- Docker (optional)

### Database Setup
1. Create PostgreSQL database:
```sql
CREATE DATABASE onlineboutique;
CREATE USER onlineboutique WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE onlineboutique TO onlineboutique;
```

2. Run migrations:
```bash
./gradlew :migrations:flywayMigrateDev
```

### Running Services

1. **Start Product Catalog Service**:
```bash
./gradlew :product-catalog-service:bootRun
```

2. **Start Currency Service**:
```bash
./gradlew :currency-service:bootRun
```

3. **Start Cart Service**:
```bash
./gradlew :cart-service:bootRun
```

4. **Start Checkout Service**:
```bash
./gradlew :checkout-service:bootRun
```

5. **Start Frontend Service**:
```bash
./gradlew :frontend:bootRun
```

### Access the Application
- Frontend: http://localhost:8080
- Product Catalog API: http://localhost:3550/api/v1/products
- Currency API: http://localhost:3551/api/v1/currency/supported
- Cart API: http://localhost:3552/api/v1/cart/{userId}
- Checkout API: http://localhost:3553/api/v1/checkout/place-order

## API Endpoints

### Product Catalog Service
- `GET /api/v1/products` - List all products
- `GET /api/v1/products/{id}` - Get product by ID
- `GET /api/v1/products/search?query={query}` - Search products

### Currency Service
- `GET /api/v1/currency/supported` - Get supported currencies
- `POST /api/v1/currency/convert` - Convert between currencies

### Cart Service
- `GET /api/v1/cart/{userId}` - Get user's cart
- `POST /api/v1/cart/{userId}/items` - Add item to cart
- `DELETE /api/v1/cart/{userId}` - Empty cart

### Checkout Service
- `POST /api/v1/checkout/place-order` - Place order

## Database Schema

### Products Table
```sql
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
```

### Currency Rates Table
```sql
CREATE TABLE currency_rates (
    currency_code VARCHAR(3) PRIMARY KEY,
    rate_to_eur DECIMAL(20,8) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Cart Items Table
```sql
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id)
);
```

## Migration Notes

### Key Changes from Original
1. **Communication**: gRPC → REST APIs
2. **Database**: Redis/Spanner/AlloyDB → PostgreSQL
3. **Language**: Multi-language → Java 17
4. **Framework**: Native → Spring Boot
5. **Observability**: OpenTelemetry → Micrometer

### Preserved Functionality
- All business logic preserved
- Same API contracts (adapted to REST)
- Same data models
- Same user flows
- Same observability features

## Development

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test
```

### Running Tests with Testcontainers
```bash
./gradlew test -Dspring.profiles.active=test
```

## Docker Support

### Building Images
```bash
docker build -t onlineboutique/frontend ./frontend
docker build -t onlineboutique/product-catalog ./product-catalog-service
docker build -t onlineboutique/currency ./currency-service
docker build -t onlineboutique/cart ./cart-service
docker build -t onlineboutique/checkout ./checkout-service
```

### Docker Compose
```bash
docker-compose up -d
```

## Monitoring and Observability

- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`
- **Logs**: Structured JSON logging

## Contributing

1. Follow Java coding standards
2. Write unit tests for new features
3. Update documentation
4. Ensure all tests pass

## License

Apache License 2.0 - Same as original project
