# IMPLEMENTATION_MANUAL.md - Manual de Implementación y Despliegue

## Resumen Ejecutivo

Este manual proporciona instrucciones completas para implementar y desplegar la aplicación **Online Boutique** migrada a Spring Boot. La migración convierte una aplicación de microservicios multi-lenguaje (Go, C#, Node.js, Python, Java) a una arquitectura unificada basada en **Java 17 LTS + Spring Boot + PostgreSQL v16 + Docker Alpine**.

## Tabla de Contenidos

1. [Prerrequisitos](#prerrequisitos)
2. [Instalación Local](#instalación-local)
3. [Configuración de Base de Datos](#configuración-de-base-de-datos)
4. [Construcción y Ejecución](#construcción-y-ejecución)
5. [Despliegue con Docker](#despliegue-con-docker)
6. [Despliegue en Kubernetes](#despliegue-en-kubernetes)
7. [Despliegue con Helm](#despliegue-con-helm)
8. [Monitoreo y Observabilidad](#monitoreo-y-observabilidad)
9. [Troubleshooting](#troubleshooting)
10. [Rollback y Recuperación](#rollback-y-recuperación)
11. [Operaciones de Producción](#operaciones-de-producción)

## Prerrequisitos

### Software Requerido

| Componente | Versión Mínima | Versión Recomendada | Notas |
|------------|----------------|---------------------|-------|
| Java | 17 LTS | 17.0.9+ | OpenJDK o Eclipse Temurin |
| Gradle | 8.5+ | 8.5+ | Build tool |
| PostgreSQL | 16.0+ | 16.1+ | Base de datos |
| Docker | 20.10+ | 24.0+ | Contenedores |
| Docker Compose | 2.0+ | 2.21+ | Orquestación local |
| kubectl | 1.28+ | 1.29+ | Kubernetes CLI |
| Helm | 3.12+ | 3.13+ | Package manager |

### Hardware Mínimo

- **CPU**: 4 cores
- **RAM**: 8GB
- **Disco**: 50GB SSD
- **Red**: 100 Mbps

### Hardware Recomendado para Producción

- **CPU**: 8+ cores
- **RAM**: 16GB+
- **Disco**: 100GB+ SSD
- **Red**: 1 Gbps

## Instalación Local

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd migrated-springboot
```

### 2. Verificar Prerrequisitos

```bash
# Verificar Java
java -version
# Debe mostrar Java 17+

# Verificar Gradle
./gradlew --version
# Debe mostrar Gradle 8.5+

# Verificar PostgreSQL
psql --version
# Debe mostrar PostgreSQL 16+
```

### 3. Configurar Variables de Entorno

```bash
# Crear archivo .env
cat > .env << EOF
DB_USERNAME=onlineboutique
DB_PASSWORD=password
DATABASE_URL=jdbc:postgresql://localhost:5432/onlineboutique
SPRING_PROFILES_ACTIVE=dev
EOF
```

## Configuración de Base de Datos

### 1. Instalar PostgreSQL

#### Ubuntu/Debian
```bash
sudo apt update
sudo apt install postgresql-16 postgresql-client-16
```

#### CentOS/RHEL
```bash
sudo dnf install postgresql16-server postgresql16
```

#### macOS
```bash
brew install postgresql@16
```

#### Windows
Descargar desde: https://www.postgresql.org/download/windows/

### 2. Configurar PostgreSQL

```bash
# Iniciar PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Crear usuario y base de datos
sudo -u postgres psql
```

```sql
-- En psql
CREATE DATABASE onlineboutique;
CREATE USER onlineboutique WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE onlineboutique TO onlineboutique;
\q
```

### 3. Ejecutar Migraciones

```bash
# Ejecutar migraciones Flyway
./gradlew :migrations:flywayMigrateDev
```

### 4. Verificar Datos

```bash
# Conectar a la base de datos
psql -h localhost -U onlineboutique -d onlineboutique

# Verificar tablas
\dt

# Verificar productos
SELECT COUNT(*) FROM products;

# Verificar monedas
SELECT COUNT(*) FROM currency_rates;

# Salir
\q
```

## Construcción y Ejecución

### 1. Construir el Proyecto

```bash
# Construir proyecto completo
./gradlew build -x test

# Construir servicios individuales
./gradlew :product-catalog-service:bootJar -x test
./gradlew :currency-service:bootJar -x test
./gradlew :cart-service:bootJar -x test
./gradlew :checkout-service:bootJar -x test
./gradlew :frontend:bootJar -x test
```

### 2. Ejecutar Servicios Individualmente

#### Product Catalog Service
```bash
./gradlew :product-catalog-service:bootRun
# Servicio disponible en: http://localhost:3550
```

#### Currency Service
```bash
./gradlew :currency-service:bootRun
# Servicio disponible en: http://localhost:3551
```

#### Cart Service
```bash
./gradlew :cart-service:bootRun
# Servicio disponible en: http://localhost:3552
```

#### Checkout Service
```bash
./gradlew :checkout-service:bootRun
# Servicio disponible en: http://localhost:3553
```

#### Frontend Service
```bash
./gradlew :frontend:bootRun
# Aplicación disponible en: http://localhost:8080
```

### 3. Verificar Servicios

```bash
# Verificar health checks
curl http://localhost:3550/actuator/health
curl http://localhost:3551/actuator/health
curl http://localhost:3552/actuator/health
curl http://localhost:3553/actuator/health
curl http://localhost:8080/actuator/health

# Verificar productos
curl http://localhost:3550/api/v1/products

# Verificar monedas
curl http://localhost:3551/api/v1/currency/supported
```

## Despliegue con Docker

### 1. Construir Imágenes Docker

#### Usando Script de Build
```bash
# Linux/macOS
./build.sh

# Windows
build.bat
```

#### Construcción Manual
```bash
# Construir imágenes individuales
docker build -f frontend/Dockerfile -t onlineboutique/frontend:latest .
docker build -f product-catalog-service/Dockerfile -t onlineboutique/product-catalog-service:latest .
docker build -f currency-service/Dockerfile -t onlineboutique/currency-service:latest .
docker build -f cart-service/Dockerfile -t onlineboutique/cart-service:latest .
docker build -f checkout-service/Dockerfile -t onlineboutique/checkout-service:latest .
```

### 2. Ejecutar con Docker Compose

```bash
# Iniciar todos los servicios
docker-compose up -d

# Verificar estado
docker-compose ps

# Ver logs
docker-compose logs -f

# Parar servicios
docker-compose down
```

### 3. Verificar Despliegue Docker

```bash
# Verificar contenedores
docker ps

# Verificar conectividad
curl http://localhost:8080/actuator/health
curl http://localhost:3550/api/v1/products

# Acceder a la aplicación
open http://localhost:8080
```

## Despliegue en Kubernetes

### 1. Configurar kubectl

```bash
# Verificar conexión
kubectl cluster-info

# Verificar nodos
kubectl get nodes
```

### 2. Aplicar Manifiestos

```bash
# Crear namespace y PostgreSQL
kubectl apply -f k8s/postgres.yaml

# Esperar a que PostgreSQL esté listo
kubectl wait --for=condition=ready pod -l app=postgres -n onlineboutique --timeout=300s

# Aplicar servicios
kubectl apply -f k8s/services.yaml

# Verificar despliegue
kubectl get pods -n onlineboutique
kubectl get services -n onlineboutique
```

### 3. Configurar Ingress

```bash
# Aplicar ingress (requiere nginx-ingress)
kubectl apply -f k8s/ingress.yaml

# Verificar ingress
kubectl get ingress -n onlineboutique
```

### 4. Verificar Despliegue Kubernetes

```bash
# Obtener IP del servicio
kubectl get service frontend -n onlineboutique

# Port forward para testing
kubectl port-forward service/frontend 8080:8080 -n onlineboutique

# Verificar aplicación
curl http://localhost:8080/actuator/health
```

## Despliegue con Helm

### 1. Instalar Helm Chart

```bash
# Agregar repositorio (si es necesario)
helm repo add bitnami https://charts.bitnami.com/bitnami

# Instalar chart
helm install onlineboutique ./helm-chart \
  --namespace onlineboutique \
  --create-namespace \
  --values ./helm-chart/values.yaml
```

### 2. Configurar Valores Personalizados

```bash
# Crear values personalizados
cat > values-production.yaml << EOF
replicaCount: 3

postgresql:
  auth:
    password: "secure-password"
  primary:
    persistence:
      size: 50Gi

services:
  product-catalog:
    replicaCount: 3
  currency:
    replicaCount: 3
  cart:
    replicaCount: 3
  checkout:
    replicaCount: 3

ingress:
  enabled: true
  hosts:
    - host: onlineboutique.yourdomain.com
      paths:
        - path: /
          pathType: Prefix
EOF

# Instalar con valores personalizados
helm install onlineboutique-prod ./helm-chart \
  --namespace onlineboutique-prod \
  --create-namespace \
  --values values-production.yaml
```

### 3. Actualizar Despliegue

```bash
# Actualizar chart
helm upgrade onlineboutique ./helm-chart \
  --namespace onlineboutique \
  --values values-production.yaml

# Verificar actualización
helm status onlineboutique -n onlineboutique
```

## Monitoreo y Observabilidad

### 1. Métricas de Aplicación

```bash
# Verificar métricas Prometheus
curl http://localhost:8080/actuator/prometheus

# Verificar métricas de cada servicio
curl http://localhost:3550/actuator/prometheus
curl http://localhost:3551/actuator/prometheus
curl http://localhost:3552/actuator/prometheus
curl http://localhost:3553/actuator/prometheus
```

### 2. Health Checks

```bash
# Health checks detallados
curl http://localhost:8080/actuator/health
curl http://localhost:3550/actuator/health
curl http://localhost:3551/actuator/health
curl http://localhost:3552/actuator/health
curl http://localhost:3553/actuator/health
```

### 3. Logs

```bash
# Docker Compose
docker-compose logs -f frontend
docker-compose logs -f product-catalog-service

# Kubernetes
kubectl logs -f deployment/frontend -n onlineboutique
kubectl logs -f deployment/product-catalog-service -n onlineboutique
```

### 4. Configurar Prometheus y Grafana

```bash
# Instalar Prometheus
helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace

# Configurar ServiceMonitor
kubectl apply -f monitoring/service-monitor.yaml
```

## Troubleshooting

### Problemas Comunes

#### 1. Error de Conexión a Base de Datos

**Síntomas:**
```
Connection refused to database
```

**Solución:**
```bash
# Verificar PostgreSQL
sudo systemctl status postgresql
sudo systemctl start postgresql

# Verificar conexión
psql -h localhost -U onlineboutique -d onlineboutique

# Verificar variables de entorno
echo $DATABASE_URL
```

#### 2. Error de Puerto en Uso

**Síntomas:**
```
Port 8080 is already in use
```

**Solución:**
```bash
# Encontrar proceso usando el puerto
lsof -i :8080
netstat -tulpn | grep :8080

# Matar proceso
kill -9 <PID>

# O cambiar puerto en application.yml
```

#### 3. Error de Memoria Insuficiente

**Síntomas:**
```
OutOfMemoryError
```

**Solución:**
```bash
# Aumentar heap size
export JAVA_OPTS="-Xmx1g -Xms512m"

# O en Docker
docker run -e JAVA_OPTS="-Xmx1g -Xms512m" onlineboutique/frontend
```

#### 4. Error de Migración de Base de Datos

**Síntomas:**
```
Flyway migration failed
```

**Solución:**
```bash
# Verificar estado de migraciones
./gradlew :migrations:flywayInfo

# Reparar migraciones
./gradlew :migrations:flywayRepair

# Ejecutar migraciones manualmente
./gradlew :migrations:flywayMigrateDev
```

### Logs de Debugging

```bash
# Habilitar debug logging
export SPRING_PROFILES_ACTIVE=dev
export LOGGING_LEVEL_COM_ONLINEBOUTIQUE=DEBUG

# Ver logs detallados
docker-compose logs -f --tail=100
```

## Rollback y Recuperación

### 1. Rollback de Docker

```bash
# Listar imágenes
docker images | grep onlineboutique

# Rollback a versión anterior
docker-compose down
docker-compose up -d --scale frontend=0
docker-compose up -d --scale frontend=1

# O usar imagen específica
docker-compose -f docker-compose.yml -f docker-compose.rollback.yml up -d
```

### 2. Rollback de Kubernetes

```bash
# Ver historial de despliegues
kubectl rollout history deployment/frontend -n onlineboutique

# Rollback a versión anterior
kubectl rollout undo deployment/frontend -n onlineboutique

# Rollback específico
kubectl rollout undo deployment/frontend --to-revision=2 -n onlineboutique
```

### 3. Rollback de Helm

```bash
# Ver historial
helm history onlineboutique -n onlineboutique

# Rollback
helm rollback onlineboutique 1 -n onlineboutique
```

### 4. Recuperación de Base de Datos

```bash
# Backup
pg_dump -h localhost -U onlineboutique onlineboutique > backup.sql

# Restore
psql -h localhost -U onlineboutique onlineboutique < backup.sql

# Verificar integridad
psql -h localhost -U onlineboutique onlineboutique -c "SELECT COUNT(*) FROM products;"
```

## Operaciones de Producción

### 1. Escalado Horizontal

#### Docker Compose
```bash
# Escalar servicios
docker-compose up -d --scale frontend=3
docker-compose up -d --scale product-catalog-service=3
```

#### Kubernetes
```bash
# Escalar deployments
kubectl scale deployment frontend --replicas=3 -n onlineboutique
kubectl scale deployment product-catalog-service --replicas=3 -n onlineboutique
```

#### Helm
```bash
# Actualizar valores
helm upgrade onlineboutique ./helm-chart \
  --set replicaCount=3 \
  --set services.product-catalog.replicaCount=3 \
  -n onlineboutique
```

### 2. Actualizaciones Sin Downtime

#### Blue-Green Deployment
```bash
# Desplegar nueva versión
kubectl apply -f k8s/services-v2.yaml

# Cambiar tráfico gradualmente
kubectl patch service frontend -p '{"spec":{"selector":{"version":"v2"}}}'
```

#### Rolling Update
```bash
# Actualizar imagen
kubectl set image deployment/frontend frontend=onlineboutique/frontend:v2.0 -n onlineboutique

# Verificar rollout
kubectl rollout status deployment/frontend -n onlineboutique
```

### 3. Monitoreo de Producción

#### Configurar Alertas
```yaml
# alerting-rules.yaml
groups:
- name: onlineboutique
  rules:
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: "High error rate detected"
```

#### Dashboard de Grafana
```json
{
  "dashboard": {
    "title": "Online Boutique",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])"
          }
        ]
      }
    ]
  }
}
```

### 4. Mantenimiento Programado

#### Backup Automático
```bash
#!/bin/bash
# backup.sh
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -h localhost -U onlineboutique onlineboutique > "backup_${DATE}.sql"
aws s3 cp "backup_${DATE}.sql" s3://onlineboutique-backups/
```

#### Limpieza de Logs
```bash
#!/bin/bash
# cleanup-logs.sh
find /var/log -name "*.log" -mtime +30 -delete
docker system prune -f
```

### 5. Runbooks Operacionales

#### Incident Response
1. **Identificar el problema**
   ```bash
   kubectl get pods -n onlineboutique
   kubectl describe pod <pod-name> -n onlineboutique
   ```

2. **Verificar logs**
   ```bash
   kubectl logs <pod-name> -n onlineboutique --tail=100
   ```

3. **Escalar si es necesario**
   ```bash
   kubectl scale deployment <service> --replicas=5 -n onlineboutique
   ```

4. **Rollback si es crítico**
   ```bash
   kubectl rollout undo deployment/<service> -n onlineboutique
   ```

#### Performance Tuning
1. **Ajustar recursos**
   ```yaml
   resources:
     requests:
       memory: "512Mi"
       cpu: "500m"
     limits:
       memory: "1Gi"
       cpu: "1000m"
   ```

2. **Configurar JVM**
   ```bash
   JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport"
   ```

3. **Optimizar base de datos**
   ```sql
   -- Crear índices adicionales
   CREATE INDEX CONCURRENTLY idx_products_price ON products (price_usd_units, price_usd_nanos);
   ```

## Conclusión

Este manual proporciona una guía completa para implementar y operar la aplicación Online Boutique migrada a Spring Boot. La migración preserva toda la funcionalidad original mientras moderniza la stack tecnológica y mejora la mantenibilidad.

Para soporte adicional, consulte:
- [Documentación de Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación de PostgreSQL](https://www.postgresql.org/docs/)
- [Documentación de Kubernetes](https://kubernetes.io/docs/)
- [Documentación de Helm](https://helm.sh/docs/)

---

**Versión del Manual**: 1.0.0  
**Última Actualización**: $(date)  
**Mantenido por**: Equipo de Migración Online Boutique
