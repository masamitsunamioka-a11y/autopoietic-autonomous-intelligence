@echo off
chcp 65001 >nul
setlocal
set ANTHROPIC_API_KEY=%ANTHROPIC_API_KEY%
echo [AAI] Building...
call mvnw.cmd clean install -DskipTests
if %errorlevel% neq 0 (
    echo [AAI] Build failed.
    pause
    exit /b %errorlevel%
)
echo [AAI] Creating directories...
if not exist filesystem\neural\areas mkdir filesystem\neural\areas
if not exist filesystem\neural\neurons mkdir filesystem\neural\neurons
if not exist logs mkdir logs
echo [AAI] Packaging WildFly Bootable JAR...
call mvnw.cmd wildfly-jar:package -f adapters\driving\api\pom.xml -Dmaven.test.skip=true
if %errorlevel% neq 0 (
    echo [AAI] Package failed.
    pause
    exit /b %errorlevel%
)
echo [AAI] Starting...
java -jar adapters\driving\api\target\api-1.0.0-SNAPSHOT-bootable.jar
pause
