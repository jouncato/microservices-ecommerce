# 01-inventory.md - Inventario Completo del Código Fuente

## Resumen Ejecutivo

Este documento presenta el inventario completo del código fuente del proyecto **Online Boutique** (microservices-ecommerce), una aplicación de demostración de microservicios de comercio electrónico desarrollada por Google Cloud Platform. La aplicación está compuesta por 11 microservicios escritos en diferentes lenguajes de programación que se comunican mediante gRPC.

## Arquitectura General

### Tecnologías Principales
- **Comunicación**: gRPC con Protocol Buffers
- **Frontend**: Go con Gorilla Mux
- **Backend Services**: Go, C#, Node.js, Python, Java
- **Bases de Datos**: Redis (carrito), JSON files (productos), múltiples opciones (Spanner, AlloyDB)
- **Observabilidad**: OpenTelemetry, Google Cloud Profiler, Logging estructurado
- **Contenedores**: Docker con múltiples imágenes base
- **Orquestación**: Kubernetes, Helm, Kustomize, Terraform

## Inventario Detallado por Microservicio

### 1. Frontend Service (Go)
**Ubicación**: `src/frontend/`
**Propósito**: Servidor HTTP que sirve la interfaz web de la aplicación

#### Archivos Principales:
- `main.go` - Punto de entrada principal, configuración de servidor HTTP
- `handlers.go` - Manejadores HTTP para todas las rutas web
- `rpc.go` - Clientes gRPC para comunicación con otros servicios
- `middleware.go` - Middleware para logging, sesiones y tracing
- `money/money.go` - Utilidades para manejo de monedas
- `validator/validator.go` - Validaciones de entrada
- `deployment_details.go` - Detección de plataforma de despliegue
- `packaging_info.go` - Información de empaquetado

#### Endpoints HTTP:
- `GET /` - Página principal
- `GET /product/{id}` - Vista de producto individual
- `GET /cart` - Vista del carrito
- `POST /cart` - Agregar producto al carrito
- `POST /cart/empty` - Vaciar carrito
- `POST /setCurrency` - Cambiar moneda
- `GET /logout` - Cerrar sesión
- `POST /cart/checkout` - Procesar pedido
- `GET /assistant` - Asistente de compras
- `POST /bot` - Chat bot
- `GET /product-meta/{ids}` - Metadatos de productos
- `GET /_healthz` - Health check
- `GET /robots.txt` - Robots.txt
- `GET /static/*` - Archivos estáticos

#### Dependencias:
- `github.com/gorilla/mux` - Router HTTP
- `github.com/sirupsen/logrus` - Logging
- `go.opentelemetry.io/` - Tracing distribuido
- `cloud.google.com/go/profiler` - Profiling
- `google.golang.org/grpc` - Cliente gRPC

### 2. Cart Service (C#)
**Ubicación**: `src/cartservice/`
**Propósito**: Gestión del carrito de compras con persistencia en Redis/Spanner/AlloyDB

#### Archivos Principales:
- `src/Program.cs` - Punto de entrada de la aplicación
- `src/Startup.cs` - Configuración de servicios y middleware
- `src/services/CartService.cs` - Implementación del servicio gRPC
- `src/services/HealthCheckService.cs` - Health checks
- `src/cartstore/ICartStore.cs` - Interfaz para almacenamiento
- `src/cartstore/RedisCartStore.cs` - Implementación Redis
- `src/cartstore/SpannerCartStore.cs` - Implementación Spanner
- `src/cartstore/AlloyDBCartStore.cs` - Implementación AlloyDB
- `tests/CartServiceTests.cs` - Pruebas unitarias

#### Servicios gRPC:
- `AddItem(AddItemRequest)` - Agregar producto al carrito
- `GetCart(GetCartRequest)` - Obtener contenido del carrito
- `EmptyCart(EmptyCartRequest)` - Vaciar carrito

#### Dependencias:
- `Grpc.AspNetCore` - Servidor gRPC
- `Microsoft.Extensions.Caching.StackExchangeRedis` - Cliente Redis
- `Google.Cloud.Spanner.Data` - Cliente Spanner
- `Npgsql` - Cliente PostgreSQL
- `Google.Cloud.SecretManager.V1` - Gestión de secretos

### 3. Product Catalog Service (Go)
**Ubicación**: `src/productcatalogservice/`
**Propósito**: Catálogo de productos con búsqueda y filtrado

#### Archivos Principales:
- `server.go` - Servidor gRPC principal
- `product_catalog.go` - Lógica de negocio del catálogo
- `catalog_loader.go` - Carga de productos desde JSON
- `products.json` - Datos de productos (113 productos)
- `product_catalog_test.go` - Pruebas unitarias

#### Servicios gRPC:
- `ListProducts(Empty)` - Listar todos los productos
- `GetProduct(GetProductRequest)` - Obtener producto específico
- `SearchProducts(SearchProductsRequest)` - Buscar productos

#### Datos:
- 113 productos con categorías: accessories, clothing, footwear, hair, beauty, decor, home, kitchen
- Precios en USD con estructura Money (units + nanos)
- Imágenes estáticas en `/static/img/products/`

### 4. Currency Service (Node.js)
**Ubicación**: `src/currencyservice/`
**Propósito**: Conversión de monedas usando datos del Banco Central Europeo

#### Archivos Principales:
- `server.js` - Servidor gRPC principal
- `client.js` - Cliente de prueba
- `data/currency_conversion.json` - Tasas de cambio (35 monedas)
- `proto/demo.proto` - Definición del servicio

#### Servicios gRPC:
- `GetSupportedCurrencies(Empty)` - Obtener monedas soportadas
- `Convert(CurrencyConversionRequest)` - Convertir entre monedas

#### Dependencias:
- `@grpc/grpc-js` - Servidor gRPC
- `@google-cloud/profiler` - Profiling
- `@opentelemetry/` - Tracing distribuido
- `pino` - Logging estructurado
- `xml2js` - Parsing XML

### 5. Payment Service (Node.js)
**Ubicación**: `src/paymentservice/`
**Propósito**: Procesamiento de pagos con tarjeta de crédito (simulado)

#### Archivos Principales:
- `server.js` - Servidor gRPC principal
- `index.js` - Punto de entrada
- `charge.js` - Lógica de procesamiento de pagos
- `logger.js` - Configuración de logging
- `proto/demo.proto` - Definición del servicio

#### Servicios gRPC:
- `Charge(ChargeRequest)` - Procesar pago con tarjeta

#### Dependencias:
- `@grpc/grpc-js` - Servidor gRPC
- `simple-card-validator` - Validación de tarjetas
- `uuid` - Generación de IDs únicos
- `pino` - Logging estructurado

### 6. Shipping Service (Go)
**Ubicación**: `src/shippingservice/`
**Propósito**: Cálculo de costos de envío y procesamiento de envíos

#### Archivos Principales:
- `main.go` - Servidor gRPC principal
- `quote.go` - Lógica de cálculo de cotizaciones
- `tracker.go` - Generación de tracking IDs
- `shippingservice_test.go` - Pruebas unitarias

#### Servicios gRPC:
- `GetQuote(GetQuoteRequest)` - Obtener cotización de envío
- `ShipOrder(ShipOrderRequest)` - Procesar envío

### 7. Email Service (Python)
**Ubicación**: `src/emailservice/`
**Propósito**: Envío de confirmaciones de pedido por email

#### Archivos Principales:
- `email_server.py` - Servidor gRPC principal
- `email_client.py` - Cliente de prueba
- `logger.py` - Configuración de logging
- `templates/confirmation.html` - Template HTML para emails
- `demo_pb2.py`, `demo_pb2_grpc.py` - Código generado de Protocol Buffers

#### Servicios gRPC:
- `SendOrderConfirmation(SendOrderConfirmationRequest)` - Enviar confirmación

#### Dependencias:
- `grpcio` - Servidor gRPC
- `jinja2` - Templates HTML
- `google-cloud-profiler` - Profiling
- `opentelemetry-` - Tracing distribuido

### 8. Checkout Service (Go)
**Ubicación**: `src/checkoutservice/`
**Propósito**: Orquestación del proceso de checkout completo

#### Archivos Principales:
- `main.go` - Servidor gRPC principal
- `money/money.go` - Utilidades para manejo de monedas
- `money/money_test.go` - Pruebas de utilidades

#### Servicios gRPC:
- `PlaceOrder(PlaceOrderRequest)` - Procesar pedido completo

#### Flujo de Checkout:
1. Obtener carrito del usuario
2. Validar productos y precios
3. Procesar pago
4. Calcular envío
5. Enviar confirmación por email
6. Retornar resultado del pedido

### 9. Recommendation Service (Python)
**Ubicación**: `src/recommendationservice/`
**Propósito**: Recomendaciones de productos basadas en el carrito

#### Archivos Principales:
- `recommendation_server.py` - Servidor gRPC principal
- `client.py` - Cliente de prueba
- `logger.py` - Configuración de logging

#### Servicios gRPC:
- `ListRecommendations(ListRecommendationsRequest)` - Obtener recomendaciones

#### Algoritmo:
- Recomendaciones aleatorias basadas en categorías de productos en el carrito
- Fallback a productos populares si no hay carrito

### 10. Ad Service (Java)
**Ubicación**: `src/adservice/`
**Propósito**: Servicio de anuncios contextuales

#### Archivos Principales:
- `src/main/java/hipstershop/AdService.java` - Servidor gRPC principal
- `src/main/java/hipstershop/AdServiceClient.java` - Cliente de prueba
- `build.gradle` - Configuración de build
- `src/main/proto/demo.proto` - Definición del servicio

#### Servicios gRPC:
- `GetAds(AdRequest)` - Obtener anuncios contextuales

#### Dependencias:
- `io.grpc:grpc-*` - Servidor gRPC
- `com.google.protobuf:protobuf-java` - Protocol Buffers
- `org.apache.logging.log4j:log4j-core` - Logging

### 11. Load Generator (Python)
**Ubicación**: `src/loadgenerator/`
**Propósito**: Generación de carga para pruebas de rendimiento

#### Archivos Principales:
- `locustfile.py` - Script de Locust para generación de carga

#### Escenarios de Carga:
- Navegación por productos
- Agregar productos al carrito
- Proceso de checkout
- Simulación de usuarios concurrentes

### 12. Shopping Assistant Service (Python)
**Ubicación**: `src/shoppingassistantservice/`
**Propósito**: Asistente de compras con IA (Gemini)

#### Archivos Principales:
- `shoppingassistantservice.py` - Servicio principal con integración Gemini

## Archivos de Configuración y Despliegue

### Protocol Buffers
**Ubicación**: `protos/`
- `demo.proto` - Definición completa de todos los servicios gRPC
- `grpc/health/v1/health.proto` - Health checks

### Kubernetes Manifests
**Ubicación**: `kubernetes-manifests/`
- Manifiestos YAML para cada servicio
- Configuraciones de servicios, deployments, configmaps
- `kustomization.yaml` - Configuración de Kustomize

### Helm Charts
**Ubicación**: `helm-chart/`
- `Chart.yaml` - Metadatos del chart
- `values.yaml` - Valores por defecto
- `templates/` - Templates de Kubernetes

### Terraform
**Ubicación**: `terraform/`
- `main.tf` - Infraestructura principal
- `memorystore.tf` - Configuración Redis
- `variables.tf` - Variables de entrada
- `output.tf` - Outputs del módulo

### Skaffold
**Ubicación**: `skaffold.yaml`
- Configuración para desarrollo local
- Build y deploy automatizado
- Múltiples perfiles (gcb, debug, network-policies)

## Recursos Externos y Dependencias

### Bases de Datos
1. **Redis** (por defecto)
   - Almacenamiento del carrito de compras
   - Configuración in-cluster o Memorystore

2. **Google Cloud Spanner**
   - Opción para carrito de compras
   - Base de datos completamente gestionada

3. **AlloyDB**
   - Opción PostgreSQL gestionada
   - Para carrito de compras

4. **JSON Files**
   - Catálogo de productos estático
   - Tasas de cambio de monedas

### Servicios de Google Cloud
- **Google Cloud Profiler** - Profiling de aplicaciones
- **Google Cloud Trace** - Tracing distribuido
- **Google Cloud Operations** - Observabilidad completa
- **Memorystore** - Redis gestionado
- **Spanner** - Base de datos distribuida
- **AlloyDB** - PostgreSQL gestionado
- **Gemini** - IA para asistente de compras

### Observabilidad
- **OpenTelemetry** - Tracing distribuido
- **Structured Logging** - Logs en formato JSON
- **Health Checks** - Endpoints de salud
- **Metrics** - Métricas de aplicación

## Mapa de Responsabilidades

### Frontend (Go)
- Servir interfaz web
- Manejo de sesiones
- Comunicación con todos los servicios backend
- Conversión de monedas
- Renderizado de templates HTML

### Cart Service (C#)
- Persistencia del carrito de compras
- Operaciones CRUD del carrito
- Soporte múltiples backends (Redis, Spanner, AlloyDB)

### Product Catalog (Go)
- Catálogo de productos
- Búsqueda y filtrado
- Carga desde JSON estático

### Currency Service (Node.js)
- Conversión de monedas
- Tasas de cambio del BCE
- Soporte 35+ monedas

### Payment Service (Node.js)
- Procesamiento de pagos
- Validación de tarjetas
- Generación de transacciones

### Shipping Service (Go)
- Cálculo de costos de envío
- Generación de tracking IDs
- Procesamiento de envíos

### Email Service (Python)
- Envío de confirmaciones
- Templates HTML
- Integración con servicios de email

### Checkout Service (Go)
- Orquestación del checkout
- Coordinación entre servicios
- Manejo de transacciones

### Recommendation Service (Python)
- Algoritmos de recomendación
- Análisis del carrito
- Productos relacionados

### Ad Service (Java)
- Anuncios contextuales
- Palabras clave
- Integración con sistemas de ads

## Recursos que Requieren Adaptación Especial

### 1. Dependencias Nativas
- **Google Cloud Profiler**: Requiere agentes nativos específicos por lenguaje
- **OpenTelemetry**: Instrumentación específica por lenguaje
- **gRPC**: Generación de código específica por lenguaje

### 2. Binarios y Librerías
- **Protocol Buffers**: Compiladores específicos por lenguaje
- **gRPC**: Librerías específicas por lenguaje
- **Logging**: Librerías específicas por lenguaje

### 3. Formatos de Datos
- **JSON**: Productos y tasas de cambio
- **HTML Templates**: Templates de email
- **Protocol Buffers**: Contratos de servicios

### 4. Configuraciones Específicas
- **Variables de Entorno**: Configuración específica por servicio
- **Health Checks**: Implementación específica por lenguaje
- **Tracing**: Configuración específica por lenguaje

## Preguntas Abiertas

### 1. Funcionalidad de IA
- **Pregunta**: ¿El Shopping Assistant Service con Gemini debe migrarse completamente o es opcional?
- **Contexto**: El servicio usa Google Gemini para análisis de imágenes y recomendaciones

### 2. Observabilidad
- **Pregunta**: ¿Qué nivel de observabilidad debe mantenerse en la migración?
- **Contexto**: OpenTelemetry, Profiling, y métricas están integrados en todos los servicios

### 3. Bases de Datos
- **Pregunta**: ¿Debe mantenerse la flexibilidad de múltiples backends para el carrito?
- **Contexto**: Actualmente soporta Redis, Spanner y AlloyDB

### 4. Load Generator
- **Pregunta**: ¿El Load Generator debe migrarse o puede mantenerse en Python?
- **Contexto**: Es una herramienta de testing, no parte del negocio

### 5. Templates y Assets
- **Pregunta**: ¿Los templates HTML y assets estáticos deben migrarse o mantenerse?
- **Contexto**: Templates de email y archivos estáticos del frontend

### 6. Configuración de Despliegue
- **Pregunta**: ¿Qué nivel de flexibilidad de despliegue debe mantenerse?
- **Contexto**: Soporte para Kubernetes, Helm, Kustomize, Terraform

## Supuestos para la Migración

1. **PostgreSQL como única base de datos**: Se migrará todo a PostgreSQL v16
2. **Spring Boot como framework**: Todos los servicios se migrarán a Spring Boot
3. **Java 17 LTS**: Versión estándar para la migración
4. **Docker Alpine**: Imágenes base Alpine para todos los servicios
5. **Flyway**: Para migraciones de base de datos
6. **Maven**: Sistema de build para Java
7. **REST APIs**: Conversión de gRPC a REST APIs donde sea apropiado
8. **Observabilidad**: Mantener OpenTelemetry y logging estructurado
9. **Health Checks**: Implementar health checks estándar de Spring Boot
10. **Configuración**: Usar Spring Boot configuration properties

## Conclusión

El proyecto Online Boutique es una aplicación compleja de microservicios con múltiples tecnologías y patrones. La migración requerirá:

1. **Preservación completa de funcionalidad**: Todos los endpoints y flujos de negocio
2. **Migración de datos**: Productos, configuraciones y estructuras
3. **Adaptación de comunicación**: De gRPC a REST APIs
4. **Consolidación de bases de datos**: Todo a PostgreSQL
5. **Mantenimiento de observabilidad**: Logging, tracing y métricas
6. **Preservación de flexibilidad**: Múltiples opciones de despliegue

El inventario completo proporciona la base para el diseño de migración detallado en la siguiente fase.
