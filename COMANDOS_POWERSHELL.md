# COMANDOS POWERSHELL - SUMAQ SPA

## DETENER PROCESO EN PUERTO 8080

# Encontrar proceso
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess

# Detener proceso
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force

## O COMANDO SIMPLE

# Ver que esta usando el puerto 8080
netstat -ano | findstr :8080

# Detener (reemplaza XXXX con el PID que viste)
taskkill /F /PID XXXX

## INICIAR SERVIDOR

# Desarrollo
.\mvnw.cmd spring-boot:run

# Produccion (generar JAR)
.\mvnw.cmd clean package -DskipTests

# Ejecutar JAR
java -jar target\demo-0.0.1-SNAPSHOT.jar

## DOCKER

# Iniciar
docker-compose up -d --build

# Detener
docker-compose down

# Ver logs
docker-compose logs -f app

## LIMPIAR

# Limpiar proyecto
.\mvnw.cmd clean

# Limpiar Docker
docker-compose down -v

