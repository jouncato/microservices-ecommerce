# Product Catalog Service Dockerfile
FROM eclipse-temurin:17-jre-alpine AS base

WORKDIR /app

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

RUN apk add --no-cache curl tzdata
ENV TZ=UTC

FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY product-catalog-service/build.gradle product-catalog-service/
COPY common/build.gradle common/

COPY product-catalog-service/src product-catalog-service/src
COPY common/src common/src

RUN ./gradlew :product-catalog-service:bootJar -x test

FROM base AS runtime

COPY --from=build /app/product-catalog-service/build/libs/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 3550

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:3550/actuator/health || exit 1

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
