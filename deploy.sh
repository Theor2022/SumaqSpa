#!/bin/bash
# ========================================
# SUMAQ SPA - Despliegue en Servidor Linux
# ========================================

echo "========================================="
echo "   SUMAQ SPA - DESPLIEGUE PRODUCCION"
echo "========================================="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Variables
APP_NAME="sumaqspa"
JAR_FILE="demo-0.0.1-SNAPSHOT.jar"
APP_PORT=8080
JAVA_OPTS="-Xmx512m -Xms256m"
PROFILE="prod"

echo "Configuracion:"
echo "  App: $APP_NAME"
echo "  Puerto: $APP_PORT"
echo "  Perfil: $PROFILE"
echo ""

# Verificar que existe el JAR
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}[ERROR]${NC} No se encuentra el archivo $JAR_FILE"
    echo "Ejecuta primero: ./mvnw clean package -DskipTests"
    exit 1
fi

echo -e "${GREEN}[OK]${NC} Archivo JAR encontrado"

# Detener instancia anterior si existe
echo ""
echo "[1/5] Deteniendo instancia anterior..."
if [ -f "app.pid" ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        kill $PID
        echo -e "${GREEN}[OK]${NC} Proceso anterior detenido (PID: $PID)"
        sleep 2
    fi
    rm -f app.pid
fi

# Crear directorio de logs
echo ""
echo "[2/5] Creando directorio de logs..."
mkdir -p logs
echo -e "${GREEN}[OK]${NC} Directorio creado"

# Backup del JAR anterior
echo ""
echo "[3/5] Haciendo backup del JAR anterior..."
if [ -f "$APP_NAME.jar" ]; then
    mv $APP_NAME.jar $APP_NAME.jar.backup.$(date +%Y%m%d_%H%M%S)
    echo -e "${GREEN}[OK]${NC} Backup creado"
fi

# Copiar nuevo JAR
echo ""
echo "[4/5] Copiando nuevo JAR..."
cp $JAR_FILE $APP_NAME.jar
echo -e "${GREEN}[OK]${NC} JAR copiado"

# Iniciar aplicacion
echo ""
echo "[5/5] Iniciando aplicacion..."
nohup java $JAVA_OPTS \
    -Dspring.profiles.active=$PROFILE \
    -Dserver.port=$APP_PORT \
    -jar $APP_NAME.jar \
    > logs/app.log 2>&1 &

# Guardar PID
echo $! > app.pid
PID=$(cat app.pid)

echo -e "${GREEN}[OK]${NC} Aplicacion iniciada (PID: $PID)"
echo ""
echo "========================================="
echo "   DESPLIEGUE EXITOSO"
echo "========================================="
echo ""
echo "La aplicacion esta corriendo en:"
echo "  http://localhost:$APP_PORT"
echo ""
echo "Logs en tiempo real:"
echo "  tail -f logs/app.log"
echo ""
echo "Para detener:"
echo "  kill $PID"
echo "  O ejecuta: ./stop.sh"
echo ""
echo "========================================="

