#!/bin/bash

# Build script for Online Boutique Docker images
# Migrated from: Original multi-language build process

set -e

echo "Building Online Boutique Spring Boot Services..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Build common module first
print_status "Building common module..."
./gradlew :common:build -x test

# Build services
services=("product-catalog-service" "currency-service" "cart-service" "checkout-service" "frontend")

for service in "${services[@]}"; do
    print_status "Building $service..."
    
    # Build the service
    if ! ./gradlew ":$service:bootJar" -x test; then
        print_error "Failed to build $service"
        exit 1
    fi
    
    # Build Docker image
    print_status "Building Docker image for $service..."
    if ! docker build -f "$service/Dockerfile" -t "onlineboutique/$service:latest" .; then
        print_error "Failed to build Docker image for $service"
        exit 1
    fi
    
    print_status "Successfully built $service"
done

print_status "All services built successfully!"

# Optional: Run tests
if [ "$1" = "--test" ]; then
    print_status "Running tests..."
    ./gradlew test
fi

# Optional: Start with docker-compose
if [ "$1" = "--start" ]; then
    print_status "Starting services with docker-compose..."
    docker-compose up -d
    print_status "Services started! Access the application at http://localhost:8080"
fi

print_status "Build completed successfully!"
print_status "To start the services, run: docker-compose up -d"
print_status "To access the application, visit: http://localhost:8080"
