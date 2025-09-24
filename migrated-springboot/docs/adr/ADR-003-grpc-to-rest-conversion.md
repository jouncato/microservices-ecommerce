# ADR-003: Conversión de gRPC a REST APIs

## Estado
Aceptado

## Contexto
La aplicación original utiliza gRPC para comunicación entre microservicios, lo que presenta:
- **Complejidad**: Protocol Buffers y generación de código
- **Debugging**: Difícil debugging de llamadas gRPC
- **Tooling**: Herramientas limitadas para testing
- **Adopción**: Menor adopción que REST en la industria
- **Firewall**: Problemas con proxies y firewalls corporativos

## Decisión
Convertir todas las comunicaciones gRPC a **REST APIs** usando Spring Web MVC.

### Justificación
- **Simplicidad**: APIs más simples de entender y debuggear
- **Tooling**: Amplio ecosistema de herramientas (Postman, curl, etc.)
- **Adopción**: Estándar de la industria
- **Debugging**: Fácil debugging con herramientas estándar
- **Firewall**: Sin problemas de conectividad
- **Spring Boot**: Integración nativa con Spring Web MVC

## Alternativas Consideradas

### Alternativa 1: Mantener gRPC
**Pros:**
- Performance superior
- Contratos fuertemente tipados
- Streaming nativo

**Contras:**
- Complejidad de desarrollo
- Debugging difícil
- Tooling limitado
- Problemas de conectividad

### Alternativa 2: GraphQL
**Pros:**
- Flexibilidad de queries
- Contratos fuertemente tipados
- Introspection

**Contras:**
- Curva de aprendizaje
- Complejidad de implementación
- Menos adopción en microservicios

### Alternativa 3: gRPC-Gateway
**Pros:**
- Mantiene gRPC internamente
- Expone REST externamente

**Contras:**
- Complejidad adicional
- Doble mantenimiento
- Overhead de conversión

## Implementación

### Mapeo de Servicios gRPC → REST

#### Product Catalog Service
```java
// gRPC: ListProducts(Empty) → List<Product>
// REST: GET /api/v1/products

@GetMapping("/api/v1/products")
public ResponseEntity<List<Product>> listProducts()

// gRPC: GetProduct(GetProductRequest) → Product
// REST: GET /api/v1/products/{id}

@GetMapping("/api/v1/products/{id}")
public ResponseEntity<Product> getProduct(@PathVariable String id)

// gRPC: SearchProducts(SearchProductsRequest) → List<Product>
// REST: GET /api/v1/products/search?query={query}

@GetMapping("/api/v1/products/search")
public ResponseEntity<List<Product>> searchProducts(@RequestParam String query)
```

#### Currency Service
```java
// gRPC: GetSupportedCurrencies(Empty) → List<String>
// REST: GET /api/v1/currency/supported

@GetMapping("/api/v1/currency/supported")
public ResponseEntity<List<String>> getSupportedCurrencies()

// gRPC: Convert(CurrencyConversionRequest) → Money
// REST: POST /api/v1/currency/convert

@PostMapping("/api/v1/currency/convert")
public ResponseEntity<Money> convert(@RequestBody ConvertRequest request)
```

#### Cart Service
```java
// gRPC: AddItem(AddItemRequest) → Empty
// REST: POST /api/v1/cart/{userId}/items

@PostMapping("/api/v1/cart/{userId}/items")
public ResponseEntity<Void> addItem(@PathVariable String userId, @RequestBody CartItem item)

// gRPC: GetCart(GetCartRequest) → Cart
// REST: GET /api/v1/cart/{userId}

@GetMapping("/api/v1/cart/{userId}")
public ResponseEntity<List<CartItem>> getCart(@PathVariable String userId)

// gRPC: EmptyCart(EmptyCartRequest) → Empty
// REST: DELETE /api/v1/cart/{userId}

@DeleteMapping("/api/v1/cart/{userId}")
public ResponseEntity<Void> emptyCart(@PathVariable String userId)
```

### Clientes HTTP
```java
@Component
public class ServiceClients {
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public Flux<Product> getProducts() {
        return webClientBuilder.build()
                .get()
                .uri(productCatalogUrl + "/api/v1/products")
                .retrieve()
                .bodyToFlux(Product.class);
    }
}
```

### Configuración
```yaml
# application.yml
services:
  product-catalog:
    url: ${PRODUCT_CATALOG_SERVICE_URL:http://localhost:3550}
  currency:
    url: ${CURRENCY_SERVICE_URL:http://localhost:3551}
  cart:
    url: ${CART_SERVICE_URL:http://localhost:3552}
  checkout:
    url: ${CHECKOUT_SERVICE_URL:http://localhost:3553}
```

## Consecuencias

### Positivas
- **Simplicidad**: APIs más fáciles de entender
- **Tooling**: Amplio ecosistema de herramientas
- **Debugging**: Fácil debugging con curl, Postman, etc.
- **Adopción**: Estándar de la industria
- **Conectividad**: Sin problemas de firewall
- **Testing**: Testing más simple con MockMvc

### Negativas
- **Performance**: Overhead HTTP vs gRPC
- **Payload**: JSON más verboso que Protocol Buffers
- **Streaming**: Sin streaming nativo
- **Tipado**: Menos tipado fuerte que gRPC

## Mitigaciones

### Performance
- **HTTP/2**: Usar HTTP/2 para multiplexing
- **Compresión**: Gzip para reducir payload
- **Caché**: Caché HTTP para datos frecuentes
- **Connection pooling**: Reutilizar conexiones HTTP

### Tipado
- **Validación**: Bean Validation para DTOs
- **Schemas**: OpenAPI/Swagger para documentación
- **Testing**: Contract testing con Pact

## Métricas de Éxito
- **Latencia**: ≤ 100ms p95 para APIs REST
- **Throughput**: 1000+ requests/segundo
- **Disponibilidad**: 99.9% uptime
- **Documentación**: 100% APIs documentadas
- **Testing**: >90% cobertura de tests

## Seguimiento
- **Monitoreo**: Métricas de latencia y throughput
- **Documentación**: OpenAPI/Swagger actualizado
- **Testing**: Contract tests automatizados
- **Performance**: Comparación con gRPC original

---

**Fecha**: 2024-01-15  
**Autor**: Equipo de Migración Online Boutique  
**Revisores**: API Lead, Arquitecto Principal, QA Lead
