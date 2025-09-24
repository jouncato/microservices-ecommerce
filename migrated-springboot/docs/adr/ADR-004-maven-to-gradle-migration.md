# ADR-004: Migración de Maven a Gradle

## Estado
ACEPTADO

## Contexto
Durante la migración del proyecto Online Boutique de múltiples lenguajes a Spring Boot, inicialmente se utilizó Maven como herramienta de construcción. Sin embargo, el usuario solicitó cambiar todo el proyecto a Gradle.

## Decisión
Migrar completamente el sistema de construcción de Maven a Gradle.

## Alternativas Consideradas

### 1. Mantener Maven
- **Pros**: Ya estaba implementado, familiar para muchos desarrolladores Java
- **Contras**: No cumple con el requerimiento del usuario

### 2. Migrar a Gradle
- **Pros**: 
  - Build más rápido con cache incremental
  - Sintaxis más concisa y legible
  - Mejor integración con IDEs modernos
  - Soporte nativo para multi-proyectos
  - Mejor manejo de dependencias
- **Contras**: 
  - Curva de aprendizaje para equipos acostumbrados a Maven
  - Requiere migración completa de configuración

## Consecuencias

### Positivas
- Build más rápido y eficiente
- Configuración más limpia y mantenible
- Mejor experiencia de desarrollo
- Cumple con el requerimiento del usuario

### Negativas
- Requiere tiempo de migración
- Equipos necesitarán aprender Gradle si no lo conocen

## Implementación

### Archivos Migrados
- `pom.xml` → `build.gradle` (raíz)
- `*/pom.xml` → `*/build.gradle` (módulos)
- Agregado `settings.gradle`
- Agregado `gradle.properties`
- Agregado Gradle Wrapper

### Scripts Actualizados
- `build.sh` / `build.bat` - Comandos Maven → Gradle
- `test.sh` / `test.bat` - Comandos Maven → Gradle
- `.github/workflows/ci-cd.yml` - Pipeline actualizado

### Dockerfiles Actualizados
- Cambio de `mvnw` → `gradlew`
- Cambio de `target/` → `build/libs/`
- Comandos de build actualizados

### Documentación Actualizada
- README.md
- IMPLEMENTATION_MANUAL.md
- MIGRATION_MAPPING.csv

## Comandos Equivalentes

| Maven | Gradle |
|-------|--------|
| `mvn clean install` | `./gradlew build` |
| `mvn test` | `./gradlew test` |
| `mvn spring-boot:run` | `./gradlew bootRun` |
| `mvn package` | `./gradlew bootJar` |
| `mvn flyway:migrate` | `./gradlew flywayMigrate` |

## Fecha de Decisión
2024-12-19

## Revisión
Esta decisión será revisada si surgen problemas significativos con Gradle o si el equipo requiere volver a Maven por razones específicas.
