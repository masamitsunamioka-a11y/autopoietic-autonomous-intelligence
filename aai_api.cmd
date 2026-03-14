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
echo [AAI] Starting...
set MAVEN_OPTS=--add-opens java.base/java.lang.reflect=ALL-UNNAMED
call mvnw.cmd -pl adapters/driving/api exec:exec
pause
