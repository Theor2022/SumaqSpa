@echo off
title Detener Servidor en Puerto 8080
color 0C
cls
echo.
echo ========================================
echo   DETENIENDO SERVIDOR EN PUERTO 8080
echo ========================================
echo.

echo Buscando proceso en puerto 8080...
echo.

for /f "tokens=5" %%a in ('netstat -aon ^| find ":8080" ^| find "LISTENING"') do (
    set PID=%%a
    echo Proceso encontrado: PID %%a
    echo.
    echo Deteniendo proceso...
    taskkill /F /PID %%a
    echo.
    echo [OK] Proceso detenido
    goto :done
)

echo [!] No se encontro ningun proceso en el puerto 8080
echo.

:done
echo.
echo ========================================
echo   COMPLETADO
echo ========================================
echo.
echo Ahora puedes ejecutar:
echo   mvnw.cmd spring-boot:run
echo.
pause

