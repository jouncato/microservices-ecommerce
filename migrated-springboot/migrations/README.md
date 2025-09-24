# Database Migration Scripts

This directory contains Flyway migration scripts for the Online Boutique database.

## Migration Files

- `V1__init_schema.sql` - Initial database schema creation
- `V2__seed_products.sql` - Product catalog data
- `V3__seed_currencies.sql` - Currency exchange rates
- `V4__seed_ads.sql` - Advertisement data

## Running Migrations

### Development
```bash
mvn flyway:migrate -Pdev
```

### Test
```bash
mvn flyway:migrate -Ptest
```

### Production
```bash
mvn flyway:migrate -Pprod
```

## Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE onlineboutique;
CREATE USER onlineboutique WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE onlineboutique TO onlineboutique;
```

2. Run migrations:
```bash
mvn flyway:migrate
```

## Migration History

| Version | Description | Files Changed |
|---------|-------------|---------------|
| V1 | Initial schema | products, currency_rates, cart_items, orders, order_items, user_sessions, recommendations, ads |
| V2 | Product data | 10 sample products |
| V3 | Currency data | 33 currency rates |
| V4 | Ad data | 8 contextual ads |

## Data Sources

- **Products**: Migrated from `src/productcatalogservice/products.json`
- **Currencies**: Migrated from `src/currencyservice/data/currency_conversion.json`
- **Cart Structure**: Migrated from Redis cart service structure
- **Orders**: New structure for checkout service requirements
- **Ads**: New structure for ad service requirements
