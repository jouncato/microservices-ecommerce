@echo off
REM Test script for Online Boutique Spring Boot Services (Windows)
REM Migrated from: Original multi-language test processes

setlocal enabledelayedexpansion

echo Running Online Boutique Spring Boot Tests...

REM Test configuration
set TEST_PROFILE=test
set COVERAGE_THRESHOLD=70

REM Function to run tests for a specific service
:run_service_tests
set service=%1
echo [TEST] Running tests for %service%...

if exist "%service%" (
    cd "%service%"
    call ..\gradlew test -Dspring.profiles.active=%TEST_PROFILE%
    if !errorlevel! neq 0 (
        echo [ERROR] Tests failed for %service%
        exit /b 1
    )
    echo [INFO] Tests passed for %service%
    cd ..
) else (
    echo [WARN] Service directory %service% not found, skipping tests
)

goto :eof

REM Function to run integration tests
:run_integration_tests
echo [TEST] Running integration tests...

call gradlew test -Dtest.single="*IntegrationTest" -Dspring.profiles.active=%TEST_PROFILE%
if !errorlevel! neq 0 (
    echo [ERROR] Integration tests failed
    exit /b 1
)
echo [INFO] Integration tests passed

goto :eof

REM Function to run contract tests
:run_contract_tests
echo [TEST] Running contract tests...

call gradlew test -Dtest.single="*ContractTest" -Dspring.profiles.active=%TEST_PROFILE%
if !errorlevel! neq 0 (
    echo [ERROR] Contract tests failed
    exit /b 1
)
echo [INFO] Contract tests passed

goto :eof

REM Function to generate test coverage report
:generate_coverage_report
echo [TEST] Generating test coverage report...

call gradlew jacocoTestReport
if !errorlevel! neq 0 (
    echo [ERROR] Failed to generate coverage report
    exit /b 1
)
echo [INFO] Coverage report generated

goto :eof

REM Main test execution
:main
echo [INFO] Starting test suite for Online Boutique Spring Boot Services

REM Run unit tests for each service
set services=product-catalog-service currency-service cart-service checkout-service frontend

for %%s in (%services%) do (
    call :run_service_tests %%s
    if !errorlevel! neq 0 (
        echo [ERROR] Test suite failed at %%s
        exit /b 1
    )
)

REM Run integration tests
call :run_integration_tests
if !errorlevel! neq 0 (
    echo [ERROR] Integration tests failed
    exit /b 1
)

REM Run contract tests
call :run_contract_tests
if !errorlevel! neq 0 (
    echo [ERROR] Contract tests failed
    exit /b 1
)

REM Generate coverage report
call :generate_coverage_report
if !errorlevel! neq 0 (
    echo [ERROR] Coverage report generation failed
    exit /b 1
)

echo [INFO] All tests completed successfully!
echo [INFO] Test coverage report available in build/reports/jacoco/test/html/index.html

goto :eof

REM Handle command line arguments
if "%1"=="unit" (
    echo [INFO] Running unit tests only...
    for %%s in (%services%) do (
        call :run_service_tests %%s
    )
) else if "%1"=="integration" (
    call :run_integration_tests
) else if "%1"=="contract" (
    call :run_contract_tests
) else if "%1"=="coverage" (
    call :generate_coverage_report
) else (
    call :main
)
