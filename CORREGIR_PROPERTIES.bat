@echo off
title Corrigiendo archivos .properties
color 0A
cls
echo.
echo Corrigiendo archivos .properties...
echo.

cd /d "%~dp0"
cd src\main\resources

echo Respaldando archivos viejos...
if exist application.properties ren application.properties application.properties.old
if exist application-dev.properties ren application-dev.properties application-dev.properties.old
if exist application-prod.properties ren application-prod.properties application-prod.properties.old

echo Instalando archivos nuevos...
if exist application-new.properties ren application-new.properties application.properties
if exist application-dev-new.properties ren application-dev-new.properties application-dev.properties
if exist application-prod-new.properties ren application-prod-new.properties application-prod.properties

echo.
echo [OK] Archivos corregidos
echo.
echo Ahora ejecuta:
echo   mvnw.cmd clean package -DskipTests
echo.
pause

