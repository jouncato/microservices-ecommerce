@echo off
REM Build script for Online Boutique Docker images (Windows)
REM Migrated from: Original multi-language build process

setlocal enabledelayedexpansion

echo Building Online Boutique Spring Boot Services...

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker is not running. Please start Docker and try again.
    exit /b 1
)

REM Build common module first
echo [INFO] Building common module...
call gradlew :common:build -x test
if %errorlevel% neq 0 (
    echo [ERROR] Failed to build common module
    exit /b 1
)

REM Build services
set services=product-catalog-service currency-service cart-service checkout-service frontend

for %%s in (%services%) do (
    echo [INFO] Building %%s...
    
    REM Build the service
    call gradlew ":%%s:bootJar" -x test
    if !errorlevel! neq 0 (
        echo [ERROR] Failed to build %%s
        exit /b 1
    )
    
    REM Build Docker image
    echo [INFO] Building Docker image for %%s...
    call docker build -f "%%s/Dockerfile" -t "onlineboutique/%%s:latest" .
    if !errorlevel! neq 0 (
        echo [ERROR] Failed to build Docker image for %%s
        exit /b 1
    )
    
    echo [INFO] Successfully built %%s
)

echo [INFO] All services built successfully!

REM Optional: Run tests
if "%1"=="--test" (
    echo [INFO] Running tests...
    call gradlew test
)

REM Optional: Start with docker-compose
if "%1"=="--start" (
    echo [INFO] Starting services with docker-compose...
    call docker-compose up -d
    echo [INFO] Services started! Access the application at http://localhost:8080
)

echo [INFO] Build completed successfully!
echo [INFO] To start the services, run: docker-compose up -d
echo [INFO] To access the application, visit: http://localhost:8080
