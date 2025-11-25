# 🚀 GUÍA COMPLETA DE DESPLIEGUE A PRODUCCIÓN - SUMAQ SPA

## 📋 ÍNDICE

1. [Opciones de Despliegue](#opciones-de-despliegue)
2. [Preparación Previa](#preparación-previa)
3. [Opción 1: Servidor VPS/Dedicado](#opción-1-servidor-vpsdedicado)
4. [Opción 2: Docker (Recomendado)](#opción-2-docker-recomendado)
5. [Opción 3: Heroku](#opción-3-heroku)
6. [Opción 4: AWS/Azure/GCP](#opción-4-awsazuregcp)
7. [Configuración Post-Despliegue](#configuración-post-despliegue)
8. [Mantenimiento](#mantenimiento)

---

## 🎯 OPCIONES DE DESPLIEGUE

### Opción 1: VPS/Servidor Dedicado (Linux)
**Ideal para:** Control total, servidor propio  
**Costo:** $5-20/mes (DigitalOcean, Linode, Vultr)  
**Dificultad:** Media  

### Opción 2: Docker + Docker Compose
**Ideal para:** Fácil despliegue, portabilidad  
**Costo:** Según hosting que uses  
**Dificultad:** Fácil  

### Opción 3: Heroku
**Ideal para:** Despliegue rápido y fácil  
**Costo:** Gratis hasta 1000 horas/mes  
**Dificultad:** Muy fácil  

### Opción 4: Cloud (AWS, Azure, GCP)
**Ideal para:** Escalabilidad, empresas  
**Costo:** Variable ($10-100+/mes)  
**Dificultad:** Alta  

---

## 📦 PREPARACIÓN PREVIA (OBLIGATORIO)

### PASO 1: Generar el JAR de Producción

**En tu máquina local, ejecuta:**

```cmd
build-produccion.bat
```

**Resultado esperado:**
```
[OK] Compilacion exitosa
[OK] JAR creado exitosamente
Archivo JAR creado en: target\demo-0.0.1-SNAPSHOT.jar
```

**Ubicación del archivo:**
```
C:\Users\herna\Saved Games\SumaqSpa\target\demo-0.0.1-SNAPSHOT.jar
```

---

### PASO 2: Configurar Variables de Producción

**Edita:** `src\main\resources\application-prod.properties`

**Cambia estos valores:**

```properties
# Base de datos de PRODUCCION (NO uses localhost)
spring.datasource.url=jdbc:mysql://TU_SERVIDOR_BD:3306/sumaq_spa_prod?useSSL=true&serverTimezone=UTC
spring.datasource.username=TU_USUARIO_BD
spring.datasource.password=TU_PASSWORD_BD
```

**Ejemplo con servidor externo:**
```properties
spring.datasource.url=jdbc:mysql://db.ejemplo.com:3306/sumaq_spa_prod?useSSL=true&serverTimezone=UTC
spring.datasource.username=sumaqspa_user
spring.datasource.password=Password123Seguro!
```

---

### PASO 3: Crear Base de Datos en Producción

**Conéctate a tu servidor MySQL de producción** y ejecuta:

```sql
-- Ejecuta el archivo: init-produccion.sql
-- O copia este código:

CREATE DATABASE IF NOT EXISTS sumaq_spa_prod;
USE sumaq_spa_prod;

-- (Las tablas se crean automáticamente con Hibernate)
-- O crea las tablas manualmente ejecutando init-produccion.sql
```

---

## 🖥️ OPCIÓN 1: SERVIDOR VPS/DEDICADO (LINUX)

**Plataformas recomendadas:**
- DigitalOcean (desde $5/mes)
- Linode (desde $5/mes)
- Vultr (desde $5/mes)
- Contabo (desde €4/mes)

### Requisitos del Servidor:

- **SO:** Ubuntu 22.04 LTS o similar
- **RAM:** Mínimo 2GB (recomendado 4GB)
- **CPU:** 1 vCore mínimo
- **Disco:** 20GB SSD
- **Java:** OpenJDK 17 o superior
- **MySQL:** 8.0 o superior

---

### PASO A PASO - VPS Linux:

#### 1. Conectarse al Servidor

```bash
ssh root@TU_IP_SERVIDOR
```

---

#### 2. Instalar Java

```bash
# Actualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar Java 17
sudo apt install openjdk-17-jre-headless -y

# Verificar instalación
java -version
```

**Deberías ver:** `openjdk version "17.0.x"`

---

#### 3. Instalar MySQL

```bash
# Instalar MySQL Server
sudo apt install mysql-server -y

# Iniciar MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# Configurar seguridad
sudo mysql_secure_installation
```

**Durante la configuración:**
- Set root password: **SÍ** (elige un password seguro)
- Remove anonymous users: **SÍ**
- Disallow root login remotely: **SÍ**
- Remove test database: **SÍ**
- Reload privilege tables: **SÍ**

---

#### 4. Crear Base de Datos y Usuario

```bash
# Entrar a MySQL
sudo mysql -u root -p

# Dentro de MySQL:
```

```sql
-- Crear base de datos
CREATE DATABASE sumaq_spa_prod;

-- Crear usuario para la aplicación
CREATE USER 'sumaqspa'@'localhost' IDENTIFIED BY 'TuPasswordSeguro123!';

-- Dar permisos
GRANT ALL PRIVILEGES ON sumaq_spa_prod.* TO 'sumaqspa'@'localhost';
FLUSH PRIVILEGES;

-- Verificar
SHOW DATABASES;
SELECT user, host FROM mysql.user WHERE user = 'sumaqspa';

-- Salir
EXIT;
```

---

#### 5. Crear Directorio de Aplicación

```bash
# Crear directorio
sudo mkdir -p /opt/sumaqspa/logs

# Crear usuario del sistema
sudo useradd -r -s /bin/false sumaqspa

# Dar permisos
sudo chown -R sumaqspa:sumaqspa /opt/sumaqspa
```

---

#### 6. Subir el JAR al Servidor

**Desde tu máquina local (Windows):**

```cmd
# Opción A: Usando SCP
scp target\demo-0.0.1-SNAPSHOT.jar root@TU_IP:/opt/sumaqspa/sumaqspa.jar

# Opción B: Usando WinSCP (interfaz gráfica)
# Descarga: https://winscp.net/
# Conecta al servidor y sube el JAR
```

---

#### 7. Configurar Servicio Systemd

**En el servidor Linux:**

```bash
# Crear archivo de servicio
sudo nano /etc/systemd/system/sumaqspa.service
```

**Pega este contenido:**

```ini
[Unit]
Description=SUMAQ SPA - Sistema de Reservas
After=syslog.target network.target mysql.service

[Service]
User=sumaqspa
Group=sumaqspa
WorkingDirectory=/opt/sumaqspa

ExecStart=/usr/bin/java -Xmx512m -Xms256m \
    -Dspring.profiles.active=prod \
    -Dspring.datasource.url=jdbc:mysql://localhost:3306/sumaq_spa_prod \
    -Dspring.datasource.username=sumaqspa \
    -Dspring.datasource.password=TuPasswordSeguro123! \
    -jar /opt/sumaqspa/sumaqspa.jar

StandardOutput=append:/opt/sumaqspa/logs/app.log
StandardError=append:/opt/sumaqspa/logs/error.log

Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Guarda:** Ctrl+X → Y → Enter

---

#### 8. Iniciar el Servicio

```bash
# Recargar systemd
sudo systemctl daemon-reload

# Habilitar inicio automático
sudo systemctl enable sumaqspa

# Iniciar servicio
sudo systemctl start sumaqspa

# Verificar estado
sudo systemctl status sumaqspa
```

**Deberías ver:** `Active: active (running)`

---

#### 9. Ver Logs

```bash
# Logs del servicio
sudo journalctl -u sumaqspa -f

# O ver el archivo directamente
sudo tail -f /opt/sumaqspa/logs/app.log
```

**Deberías ver:**
```
✅ Usuario administrador creado: admin / admin123
Started SumaqSpaApplication...
```

---

#### 10. Configurar Firewall

```bash
# Permitir puerto 8080
sudo ufw allow 8080/tcp

# Verificar
sudo ufw status
```

---

#### 11. Acceder a la Aplicación

**Abre en tu navegador:**
```
http://TU_IP_SERVIDOR:8080/index.html
```

**Ejemplo:**
```
http://123.45.67.89:8080/index.html
```

---

### Comandos Útiles (VPS):

```bash
# Ver estado del servicio
sudo systemctl status sumaqspa

# Reiniciar servicio
sudo systemctl restart sumaqspa

# Detener servicio
sudo systemctl stop sumaqspa

# Ver logs en tiempo real
sudo journalctl -u sumaqspa -f

# Ver ultimas 100 líneas de logs
sudo journalctl -u sumaqspa -n 100

# Verificar si el puerto está escuchando
sudo netstat -tulpn | grep 8080
```

---

## 🐳 OPCIÓN 2: DOCKER (RECOMENDADO)

**Ventajas:**
- ✅ Fácil de desplegar
- ✅ Portable
- ✅ Incluye MySQL automáticamente
- ✅ Fácil de actualizar

### Requisitos:

- Docker instalado
- Docker Compose instalado

---

### PASO A PASO - Docker:

#### 1. Verificar que tienes Docker

```bash
docker --version
docker-compose --version
```

---

#### 2. Crear archivo .env

**En el directorio del proyecto:**

```bash
cp .env.example .env
nano .env
```

**Edita los valores:**

```env
MYSQL_ROOT_PASSWORD=TuPasswordRootSeguro123!
MYSQL_USER=sumaqspa
MYSQL_PASSWORD=TuPasswordBDSeguro456!
JWT_SECRET=TuSecretoJWTMuyLargoYSeguro789!
```

---

#### 3. Construir e Iniciar

```bash
# Construir imágenes e iniciar servicios
docker-compose up -d --build
```

**Verás:**
```
Creating network "sumaqspa-network"...
Creating sumaqspa-mysql...
Creating sumaqspa-app...
```

---

#### 4. Ver Logs

```bash
# Logs de la aplicación
docker-compose logs -f app

# Logs de MySQL
docker-compose logs -f mysql
```

**Deberías ver:**
```
✅ Usuario administrador creado: admin / admin123
Started SumaqSpaApplication...
```

---

#### 5. Acceder a la Aplicación

```
http://localhost:8080/index.html
```

O si está en un servidor remoto:
```
http://TU_IP_SERVIDOR:8080/index.html
```

---

### Comandos Útiles (Docker):

```bash
# Ver servicios corriendo
docker-compose ps

# Detener servicios
docker-compose down

# Reiniciar servicios
docker-compose restart

# Ver logs
docker-compose logs -f

# Reconstruir
docker-compose up -d --build

# Eliminar todo (incluyendo datos)
docker-compose down -v

# Entrar al contenedor
docker exec -it sumaqspa-app sh

# Entrar a MySQL
docker exec -it sumaqspa-mysql mysql -u root -p
```

---

## ☁️ OPCIÓN 3: HEROKU

**Ventajas:**
- ✅ Muy fácil de desplegar
- ✅ Free tier disponible
- ✅ Gestión automática

### Requisitos:

- Cuenta en Heroku (gratis)
- Heroku CLI instalado

---

### PASO A PASO - Heroku:

#### 1. Instalar Heroku CLI

**Windows:**
- Descarga: https://devcenter.heroku.com/articles/heroku-cli
- Instala el ejecutable

**Verificar:**
```cmd
heroku --version
```

---

#### 2. Login en Heroku

```cmd
heroku login
```

---

#### 3. Crear Aplicación

```cmd
heroku create sumaqspa-tuempresa
```

---

#### 4. Agregar Base de Datos

```cmd
# Agregar MySQL (ClearDB)
heroku addons:create cleardb:ignite

# Obtener URL de conexión
heroku config:get CLEARDB_DATABASE_URL
```

**Resultado:**
```
mysql://usuario:password@hostname/database?reconnect=true
```

---

#### 5. Configurar Variables de Entorno

```cmd
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=TuSecretoMuySeguro123!
```

---

#### 6. Crear Procfile

**Crea un archivo llamado `Procfile` (sin extensión) en la raíz:**

```
web: java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/demo-0.0.1-SNAPSHOT.jar
```

---

#### 7. Desplegar

```cmd
git init
git add .
git commit -m "Initial commit"
heroku git:remote -a sumaqspa-tuempresa
git push heroku master
```

---

#### 8. Abrir Aplicación

```cmd
heroku open
```

---

## 🌐 OPCIÓN 4: HOSTING COMPARTIDO

Si solo tienes hosting compartido (cPanel), necesitarás:

1. **VPS mínimo** - El hosting compartido no soporta Java bien
2. O usar **Railway.app** (muy fácil y económico)

---

## ⚙️ CONFIGURACIÓN POST-DESPLIEGUE

### 1. Cambiar Contraseña del Admin

**IMPORTANTE:** Al desplegar en producción:

1. Accede: http://TU_DOMINIO/login.html
2. Login: admin / admin123
3. **Inmediatamente** ve a tu base de datos y ejecuta:

```sql
-- Cambiar contraseña del admin
UPDATE usuarios 
SET password = '$2a$10$NUEVA_PASSWORD_ENCRIPTADA' 
WHERE username = 'admin';
```

**O mejor, crea un endpoint en el backend para cambiar contraseña.**

---

### 2. Configurar Dominio

Si tienes un dominio (ejemplo: www.sumaqspa.com):

#### Con Nginx (proxy reverso):

**Instalar Nginx:**
```bash
sudo apt install nginx -y
```

**Configurar:**
```bash
sudo nano /etc/nginx/sites-available/sumaqspa
```

**Contenido:**
```nginx
server {
    listen 80;
    server_name www.sumaqspa.com sumaqspa.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

**Habilitar:**
```bash
sudo ln -s /etc/nginx/sites-available/sumaqspa /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

### 3. Configurar HTTPS (SSL/TLS)

**Usar Let's Encrypt (GRATIS):**

```bash
# Instalar Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtener certificado
sudo certbot --nginx -d www.sumaqspa.com -d sumaqspa.com

# Renovación automática
sudo certbot renew --dry-run
```

**Resultado:** Tu sitio ahora tiene HTTPS 🔒

---

## 📊 MONITOREO Y MANTENIMIENTO

### Ver Uso de Recursos

```bash
# CPU y RAM
top

# Espacio en disco
df -h

# Logs de la aplicación
tail -f /opt/sumaqspa/logs/app.log
```

---

### Backup de Base de Datos

**Script de backup automático:**

```bash
#!/bin/bash
# backup-db.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/sumaqspa/backups"
DB_NAME="sumaq_spa_prod"
DB_USER="sumaqspa"
DB_PASS="TuPassword"

mkdir -p $BACKUP_DIR

mysqldump -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/backup_$DATE.sql

# Mantener solo los últimos 7 backups
find $BACKUP_DIR -name "backup_*.sql" -mtime +7 -delete

echo "✅ Backup creado: backup_$DATE.sql"
```

**Automatizar con cron:**
```bash
# Editar crontab
crontab -e

# Agregar línea (backup diario a las 2 AM)
0 2 * * * /opt/sumaqspa/backup-db.sh
```

---

### Actualizar la Aplicación

**Cuando hagas cambios:**

1. **En tu máquina local:**
```cmd
build-produccion.bat
```

2. **Subir nuevo JAR al servidor:**
```cmd
scp target\demo-0.0.1-SNAPSHOT.jar root@TU_IP:/opt/sumaqspa/sumaqspa.jar.new
```

3. **En el servidor:**
```bash
# Detener aplicación
sudo systemctl stop sumaqspa

# Backup del JAR anterior
mv /opt/sumaqspa/sumaqspa.jar /opt/sumaqspa/sumaqspa.jar.backup

# Mover nuevo JAR
mv /opt/sumaqspa/sumaqspa.jar.new /opt/sumaqspa/sumaqspa.jar

# Iniciar aplicación
sudo systemctl start sumaqspa

# Verificar
sudo systemctl status sumaqspa
```

---

## 🔐 CHECKLIST DE SEGURIDAD

Antes de lanzar a producción:

- [ ] Cambié la contraseña del admin
- [ ] Cambié el JWT secret en production
- [ ] Configuré SSL/HTTPS
- [ ] La base de datos tiene password seguro
- [ ] Los logs NO muestran información sensible
- [ ] Configuré firewall (solo puertos necesarios)
- [ ] Tengo backups automáticos de la BD
- [ ] Las contraseñas NO están en el código
- [ ] application-prod.properties usa variables de entorno
- [ ] Actualicé las URLs del frontend (si cambió el dominio)

---

## 🚀 DESPLIEGUE RÁPIDO CON RAILWAY

**Railway.app** es la forma MÁS FÁCIL de desplegar:

### Pasos:

1. **Crea cuenta en:** https://railway.app
2. **Conecta tu GitHub** (sube tu proyecto a GitHub primero)
3. **New Project → Deploy from GitHub**
4. **Selecciona tu repositorio**
5. **Railway detecta automáticamente que es Spring Boot**
6. **Agrega MySQL** desde el dashboard
7. **Variables de entorno** se configuran automáticamente
8. **Deploy** → ¡Listo!

**URL generada automáticamente:**
```
https://sumaqspa-production.up.railway.app
```

---

## 📋 RESUMEN RÁPIDO

### Para Producción Rápida:

**Opción más fácil:** Railway.app o Heroku  
**Opción más control:** VPS con Ubuntu  
**Opción más profesional:** Docker Compose  

---

### Archivos Creados para Producción:

- ✅ `application-prod.properties` - Configuración producción
- ✅ `application-dev.properties` - Configuración desarrollo
- ✅ `build-produccion.bat` - Generar JAR
- ✅ `ejecutar-produccion.bat` - Ejecutar en Windows
- ✅ `deploy.sh` - Desplegar en Linux
- ✅ `stop.sh` - Detener aplicación
- ✅ `Dockerfile` - Para Docker
- ✅ `docker-compose.yml` - Orquestación completa
- ✅ `.env.example` - Variables de entorno
- ✅ `sumaqspa.service` - Servicio systemd
- ✅ `init-produccion.sql` - Inicializar BD producción
- ✅ `.gitignore` - No subir archivos sensibles

---

## 🎯 PRÓXIMO PASO INMEDIATO

**¿Qué tipo de servidor vas a usar?**

### Si tienes un VPS (DigitalOcean, etc.):
```
Ejecuta: build-produccion.bat
Luego sigue: OPCIÓN 1 - VPS
```

### Si quieres usar Docker:
```
Ejecuta: docker-compose up -d --build
```

### Si quieres lo más fácil:
```
Usa: Railway.app o Heroku
```

---

**¡Tu aplicación está lista para producción!** 🚀

**Todos los archivos necesarios han sido creados.**

