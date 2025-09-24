# ADR-002: Elección de PostgreSQL como Base de Datos Única

## Estado
Aceptado

## Contexto
La aplicación original utiliza múltiples sistemas de persistencia:
- **Redis**: Para carrito de compras
- **JSON files**: Para productos y tasas de cambio
- **Spanner/AlloyDB**: Opciones para carrito en producción

### Problemas Identificados
- **Fragmentación de datos**: Datos distribuidos en múltiples sistemas
- **Complejidad transaccional**: Dificultad para transacciones ACID
- **Consistencia**: Problemas de consistencia entre sistemas
- **Operación**: Múltiples sistemas para mantener
- **Backup/Recovery**: Estrategias complejas

## Decisión
Consolidar toda la persistencia en **PostgreSQL v16** como base de datos única.

### Justificación
- **ACID completo**: Transacciones robustas
- **JSON nativo**: Soporte excelente para datos semi-estructurados
- **Escalabilidad**: Horizontal y vertical
- **Ecosistema**: Amplio soporte en Spring Boot
- **Operación**: Un solo sistema para mantener
- **Backup**: Estrategias estándar y probadas

## Alternativas Consideradas

### Alternativa 1: Mantener Redis + PostgreSQL
**Pros:**
- Performance del carrito en Redis
- Separación de responsabilidades

**Contras:**
- Complejidad operacional
- Problemas de consistencia
- Transacciones distribuidas complejas

### Alternativa 2: Migrar a MongoDB
**Pros:**
- Documentos nativos
- Escalabilidad horizontal

**Contras:**
- Menos madurez en ecosistema Spring
- Transacciones más limitadas
- Curva de aprendizaje

### Alternativa 3: Migrar a MySQL
**Pros:**
- Amplia adopción
- Ecosistema maduro

**Contras:**
- Soporte JSON menos robusto
- Licenciamiento Oracle

## Implementación

### Esquema de Base de Datos
```sql
-- Productos
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

-- Carrito
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id)
);

-- Monedas
CREATE TABLE currency_rates (
    currency_code VARCHAR(3) PRIMARY KEY,
    rate_to_eur DECIMAL(20,8) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Migración de Datos
1. **Productos**: JSON → Tabla `products`
2. **Carrito**: Redis → Tabla `cart_items`
3. **Monedas**: JSON → Tabla `currency_rates`
4. **Órdenes**: Nueva tabla `orders` y `order_items`

### Optimizaciones
- **Índices**: Para búsquedas y filtros
- **JSONB**: Para categorías y metadatos
- **Particionado**: Para datos históricos
- **Caché**: Redis opcional para consultas frecuentes

## Consecuencias

### Positivas
- **Simplicidad**: Un solo sistema de persistencia
- **Consistencia**: Transacciones ACID completas
- **Backup**: Estrategia única y estándar
- **Operación**: Menos complejidad operacional
- **Desarrollo**: Spring Data JPA integrado

### Negativas
- **Performance**: Posible degradación vs Redis para carrito
- **Escalabilidad**: Limitaciones de PostgreSQL vs NoSQL
- **Migración**: Esfuerzo de migración de datos

## Mitigaciones

### Performance
- **Índices optimizados**: Para consultas frecuentes
- **Caché de aplicación**: Spring Cache para datos frecuentes
- **Connection pooling**: HikariCP optimizado
- **Read replicas**: Para consultas de solo lectura

### Escalabilidad
- **Particionado**: Por usuario para carrito
- **Read replicas**: Para distribución de carga
- **Connection pooling**: Para manejo eficiente de conexiones

## Métricas de Éxito
- **Latencia**: Carrito ≤ 50ms p95
- **Throughput**: 1000+ operaciones/segundo
- **Disponibilidad**: 99.9% uptime
- **Consistencia**: 100% transacciones ACID
- **Backup**: RPO ≤ 1 hora, RTO ≤ 4 horas

## Seguimiento
- **Monitoreo**: Métricas de performance PostgreSQL
- **Alertas**: Latencia y throughput
- **Optimización**: Análisis continuo de queries
- **Escalado**: Plan de escalado horizontal

---

**Fecha**: 2024-01-15  
**Autor**: Equipo de Migración Online Boutique  
**Revisores**: DBA Lead, Arquitecto Principal, DevOps Lead
