# Resumen Completo - Microservicios E-commerce (Online Boutique)

## Descripción General

**Online Boutique** es una aplicación de demostración de microservicios de comercio electrónico desarrollada por Google Cloud Platform. Es una aplicación web completa donde los usuarios pueden navegar por productos, agregarlos al carrito y realizar compras. La aplicación está diseñada para demostrar tecnologías modernas de Google Cloud como GKE, Service Mesh, gRPC, Cloud Operations, Spanner, Memorystore, AlloyDB y Gemini.

## Arquitectura de Microservicios

La aplicación está compuesta por **11 microservicios** escritos en diferentes lenguajes de programación que se comunican entre sí mediante gRPC:

### Servicios Principales

| Servicio | Lenguaje | Descripción |
|----------|----------|-------------|
| **Frontend** | Go | Servidor HTTP que sirve la interfaz web. No requiere registro/login y genera IDs de sesión automáticamente |
| **Cart Service** | C# | Almacena elementos del carrito de compras en Redis y los recupera |
| **Product Catalog Service** | Go | Proporciona lista de productos desde archivo JSON, búsqueda y productos individuales |
| **Currency Service** | Node.js | Convierte montos de dinero entre diferentes monedas usando valores reales del Banco Central Europeo |
| **Payment Service** | Node.js | Procesa pagos con tarjeta de crédito (simulado) y retorna ID de transacción |
| **Shipping Service** | Go | Calcula costos de envío basado en el carrito y envía productos (simulado) |
| **Email Service** | Python | Envía confirmaciones de pedido por email (simulado) |
| **Checkout Service** | Go | Recupera carrito del usuario, prepara pedido y orquesta pago, envío y notificación |
| **Recommendation Service** | Python | Recomienda otros productos basado en el contenido del carrito |
| **Ad Service** | Java | Proporciona anuncios de texto basados en palabras clave del contexto |
| **Load Generator** | Python/Locust | Envía continuamente requests simulando flujos realistas de compra |

## Tecnologías y Patrones Utilizados

### Comunicación entre Servicios
- **gRPC**: Protocolo principal de comunicación entre microservicios
- **Protocol Buffers**: Definición de contratos de servicios en `protos/demo.proto`
- **HTTP**: Frontend expone API REST para la interfaz web

### Observabilidad
- **OpenTelemetry**: Instrumentación para tracing distribuido
- **Google Cloud Profiler**: Profiling de aplicaciones
- **Structured Logging**: Logs estructurados con diferentes niveles de severidad
- **Health Checks**: Endpoints de salud para cada servicio

### Bases de Datos y Almacenamiento
- **Redis**: Almacenamiento del carrito de compras (por defecto en-cluster)
- **Google Cloud Memorystore**: Opción para Redis gestionado
- **Google Cloud Spanner**: Opción para base de datos completamente gestionada
- **AlloyDB**: Opción para base de datos PostgreSQL gestionada
- **JSON Files**: Catálogo de productos almacenado en archivos estáticos

## Estructura del Proyecto

```
microservices-ecommerce/
├── src/                          # Código fuente de microservicios
│   ├── frontend/                 # Frontend en Go
│   ├── cartservice/              # Servicio de carrito en C#
│   ├── productcatalogservice/    # Catálogo de productos en Go
│   ├── currencyservice/          # Conversión de monedas en Node.js
│   ├── paymentservice/           # Procesamiento de pagos en Node.js
│   ├── shippingservice/          # Servicio de envío en Go
│   ├── emailservice/             # Servicio de email en Python
│   ├── checkoutservice/          # Servicio de checkout en Go
│   ├── recommendationservice/   # Recomendaciones en Python
│   ├── adservice/                # Servicio de anuncios en Java
│   ├── loadgenerator/            # Generador de carga en Python
│   └── shoppingassistantservice/ # Asistente de compras con IA
├── protos/                       # Definiciones Protocol Buffers
├── kubernetes-manifests/         # Manifiestos Kubernetes básicos
├── helm-chart/                   # Chart de Helm para despliegue
├── kustomize/                    # Configuraciones Kustomize
├── terraform/                    # Infraestructura como código
├── istio-manifests/              # Configuraciones Istio/Service Mesh
└── docs/                         # Documentación del proyecto
```

## Definiciones de Servicios (gRPC)

### Servicios Principales Definidos en `protos/demo.proto`:

1. **CartService**: Gestión del carrito de compras
   - `AddItem`: Agregar productos al carrito
   - `GetCart`: Obtener contenido del carrito
   - `EmptyCart`: Vaciar carrito

2. **ProductCatalogService**: Catálogo de productos
   - `ListProducts`: Listar todos los productos
   - `GetProduct`: Obtener producto específico
   - `SearchProducts`: Buscar productos

3. **CurrencyService**: Conversión de monedas
   - `GetSupportedCurrencies`: Obtener monedas soportadas
   - `Convert`: Convertir entre monedas

4. **PaymentService**: Procesamiento de pagos
   - `Charge`: Procesar pago con tarjeta de crédito

5. **ShippingService**: Servicio de envío
   - `GetQuote`: Obtener cotización de envío
   - `ShipOrder`: Procesar envío de pedido

6. **EmailService**: Notificaciones por email
   - `SendOrderConfirmation`: Enviar confirmación de pedido

7. **CheckoutService**: Proceso de checkout
   - `PlaceOrder`: Procesar pedido completo

8. **RecommendationService**: Recomendaciones
   - `ListRecommendations`: Obtener recomendaciones de productos

9. **AdService**: Servicio de anuncios
   - `GetAds`: Obtener anuncios contextuales

## Opciones de Despliegue

### 1. Kubernetes Básico
- Manifiestos YAML en `kubernetes-manifests/`
- Despliegue directo con `kubectl apply`

### 2. Helm Charts
- Chart completo en `helm-chart/`
- Configuración flexible con `values.yaml`
- Soporte para diferentes variaciones

### 3. Kustomize Components
Múltiples componentes disponibles en `kustomize/components/`:

- **memorystore**: Integración con Redis gestionado
- **spanner**: Integración con Cloud Spanner
- **alloydb**: Integración con AlloyDB
- **google-cloud-operations**: Observabilidad completa
- **service-mesh-istio**: Service mesh con Istio
- **network-policies**: Políticas de red granulares
- **shopping-assistant**: Asistente IA con Gemini
- **cymbal-branding**: Branding personalizado

### 4. Terraform
- Infraestructura como código en `terraform/`
- Creación automática de clusters GKE
- Integración con servicios de Google Cloud

### 5. Skaffold
- Desarrollo local con `skaffold.yaml`
- Build y deploy automatizado
- Soporte para múltiples perfiles

## Características Técnicas Avanzadas

### Observabilidad
- **Tracing Distribuido**: OpenTelemetry con propagación de contexto
- **Profiling**: Google Cloud Profiler integrado
- **Métricas**: Exportación de métricas personalizadas
- **Logs Estructurados**: Formato JSON con timestamps RFC3339

### Seguridad
- **Network Policies**: Aislamiento de red entre servicios
- **Authorization Policies**: Control de acceso granular
- **Workload Identity**: Autenticación segura con Google Cloud
- **Seccomp Profiles**: Perfiles de seguridad de contenedores

### Escalabilidad y Resilencia
- **Health Checks**: Verificación de salud de servicios
- **Circuit Breakers**: Patrones de resilencia
- **Load Balancing**: Distribución de carga automática
- **Auto-scaling**: Escalado automático basado en métricas

## Flujo de Datos

### Flujo de Compra Típico:
1. **Frontend** → **ProductCatalogService**: Obtener productos
2. **Frontend** → **CartService**: Agregar productos al carrito
3. **Frontend** → **CurrencyService**: Convertir precios
4. **Frontend** → **CheckoutService**: Iniciar checkout
5. **CheckoutService** → **CartService**: Obtener carrito
6. **CheckoutService** → **PaymentService**: Procesar pago
7. **CheckoutService** → **ShippingService**: Calcular envío
8. **CheckoutService** → **EmailService**: Enviar confirmación

### Servicios de Soporte:
- **RecommendationService**: Sugiere productos relacionados
- **AdService**: Muestra anuncios contextuales
- **LoadGenerator**: Simula carga de usuarios reales

## Configuración y Variables de Entorno

### Variables Principales:
- `ENABLE_TRACING`: Habilita tracing distribuido
- `ENABLE_PROFILER`: Habilita profiling de aplicaciones
- `REDIS_ADDR`: Dirección del servidor Redis
- `SPANNER_PROJECT`: ID del proyecto Spanner
- `ALLOYDB_PRIMARY_IP`: IP de AlloyDB
- `BASE_URL`: URL base de la aplicación

### Configuración de Servicios:
- **Puertos**: Cada servicio usa puertos específicos (8080 para frontend, 3550 para productcatalog, etc.)
- **Recursos**: Límites de CPU y memoria definidos por servicio
- **Health Checks**: Endpoints `/health` y `/_healthz`

## Casos de Uso y Demostraciones

### Demostraciones Principales:
1. **Modernización de Aplicaciones**: Migración de monolitos a microservicios
2. **Service Mesh**: Gestión de tráfico con Istio/Anthos Service Mesh
3. **Observabilidad**: Tracing, métricas y logging distribuido
4. **Bases de Datos Gestionadas**: Spanner, Memorystore, AlloyDB
5. **IA/ML**: Asistente de compras con Gemini
6. **Seguridad**: Network policies y autorización granular

### Escenarios de Prueba:
- **Load Testing**: Generador de carga con Locust
- **Chaos Engineering**: Simulación de fallos de servicios
- **Performance Testing**: Pruebas de rendimiento bajo carga
- **Security Testing**: Validación de políticas de seguridad

## Requisitos del Sistema

### Desarrollo Local:
- Docker Desktop
- kubectl
- Skaffold 2.0.2+
- Minikube/Kind (opcional)

### Producción (GKE):
- Google Cloud Project
- GKE cluster con autopilot habilitado
- APIs de Google Cloud habilitadas
- Workload Identity (recomendado)

### Recursos Mínimos:
- **CPU**: 4 cores mínimo
- **Memoria**: 4GB mínimo
- **Disco**: 32GB mínimo
- **Red**: VPC-native con IP aliasing

## Mejores Prácticas Implementadas

### Arquitectura:
- **Domain-Driven Design**: Servicios organizados por dominio de negocio
- **API-First**: Contratos definidos con Protocol Buffers
- **Stateless Services**: Servicios sin estado para escalabilidad
- **Database per Service**: Cada servicio tiene su propio almacenamiento

### DevOps:
- **Infrastructure as Code**: Terraform para infraestructura
- **GitOps**: Kustomize para gestión de configuración
- **CI/CD**: Skaffold para desarrollo continuo
- **Monitoring**: Observabilidad completa integrada

### Seguridad:
- **Least Privilege**: Permisos mínimos necesarios
- **Defense in Depth**: Múltiples capas de seguridad
- **Zero Trust**: Verificación continua de identidad
- **Secrets Management**: Gestión segura de secretos

## Conclusión

Online Boutique es una aplicación de referencia completa que demuestra las mejores prácticas para el desarrollo de microservicios modernos. Combina múltiples tecnologías y patrones para crear una solución robusta, escalable y observable. La aplicación es ideal para:

- **Aprendizaje**: Entender arquitecturas de microservicios
- **Demostraciones**: Mostrar capacidades de Google Cloud
- **Testing**: Validar herramientas y tecnologías
- **Prototipado**: Base para aplicaciones de comercio electrónico

La flexibilidad en el despliegue y la extensibilidad mediante componentes Kustomize hacen que sea una excelente base para proyectos reales de microservicios.
