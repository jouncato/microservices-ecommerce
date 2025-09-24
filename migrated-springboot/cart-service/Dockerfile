# Cart Service Dockerfile
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
COPY cart-service/build.gradle cart-service/
COPY common/build.gradle common/

COPY cart-service/src cart-service/src
COPY common/src common/src

RUN ./gradlew :cart-service:bootJar -x test

FROM base AS runtime

COPY --from=build /app/cart-service/build/libs/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 3552

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:3552/actuator/health || exit 1

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
