@echo off
chcp 65001 >nul
setlocal
echo [AAI] Installing dependencies...
pushd adapters\driving\web
call npm install
if %errorlevel% neq 0 (
    echo [AAI] npm install failed.
    pause
    exit /b %errorlevel%
)
echo [AAI] Building...
call npm run build
if %errorlevel% neq 0 (
    echo [AAI] Build failed.
    pause
    exit /b %errorlevel%
)
echo [AAI] Starting...
call npm run serve
popd
pause
