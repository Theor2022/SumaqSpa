# 🌿 SUMAQ SPA - Sistema de Gestión de Reservas

Sistema completo de gestión de reservas para spa con autenticación, roles de usuario y panel de administración.

## 🎯 Características

- ✅ **Registro de usuarios** con validación completa
- ✅ **Login con roles** (ADMIN/USER)
- ✅ **Carrito persistente** de tratamientos
- ✅ **Validación de horarios** (no permite doble reserva)
- ✅ **Panel de administración** para gestionar reservas
- ✅ **Autocompletado de datos** del usuario en formularios
- ✅ **Barra de usuario dinámica** con botón de logout
- ✅ **Botón login inteligente** (se oculta/muestra según sesión)
- ✅ **Encriptación de contraseñas** con BCrypt
- ✅ **Tokens JWT** para autenticación

---

## 🛠️ Tecnologías

### Backend:
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Security** con JWT
- **Spring Data JPA** / Hibernate
- **MySQL 8.0**
- **Maven**

### Frontend:
- **HTML5** / **CSS3**
- **JavaScript** (Vanilla)
- **Bootstrap 5**
- **Responsive Design**

---

## 📁 Estructura del Proyecto

```
SumaqSpa/
├── src/
│   ├── main/
│   │   ├── java/com/spa/
│   │   │   ├── config/         # Configuración (Security, DataLoader)
│   │   │   ├── controller/     # Controladores REST
│   │   │   ├── dtos/           # Data Transfer Objects
│   │   │   ├── model/          # Entidades JPA
│   │   │   ├── repository/     # Repositorios Spring Data
│   │   │   └── services/       # Lógica de negocio
│   │   └── resources/
│   │       ├── static/         # Frontend (HTML/CSS/JS)
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/                   # Tests unitarios
├── target/                     # JAR compilado
├── pom.xml                     # Dependencias Maven
├── Dockerfile                  # Imagen Docker
├── docker-compose.yml          # Orquestación Docker
└── build-produccion.bat        # Script de build
```

---

## 🚀 Inicio Rápido (Desarrollo)

### 1. Requisitos:

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+

### 2. Configurar Base de Datos:

```sql
CREATE DATABASE sumaq_spa;
```

### 3. Configurar Aplicación:

Edita `src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sumaq_spa
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
```

### 4. Ejecutar:

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### 5. Acceder:

```
http://localhost:8080/index.html
```

### 6. Credenciales de Prueba:

**Administrador:**
- Usuario: `admin`
- Contraseña: `admin123`

**Usuario de prueba:**
- Usuario: `user`
- Contraseña: `user123`

---

## 📦 Despliegue a Producción

### OPCIÓN 1: Servidor VPS (Ubuntu)

#### Generar JAR:

```cmd
build-produccion.bat
```

#### Subir al servidor:

```bash
scp target/demo-0.0.1-SNAPSHOT.jar user@servidor:/opt/sumaqspa/
```

#### Ejecutar en el servidor:

```bash
# Ver guía completa en: GUIA_PRODUCCION.md
sudo systemctl start sumaqspa
```

**Documentos:** `GUIA_PRODUCCION.md`, `CHECKLIST_PRODUCCION.md`

---

### OPCIÓN 2: Docker (Recomendado)

#### 1. Crear archivo `.env`:

```env
MYSQL_ROOT_PASSWORD=password_seguro
MYSQL_USER=sumaqspa
MYSQL_PASSWORD=password_bd_seguro
JWT_SECRET=secreto_jwt_muy_largo_y_seguro
```

#### 2. Iniciar servicios:

```bash
docker-compose up -d --build
```

#### 3. Verificar:

```bash
docker-compose ps
docker-compose logs -f app
```

#### 4. Acceder:

```
http://localhost:8080
```

---

### OPCIÓN 3: Heroku

#### 1. Login:

```bash
heroku login
```

#### 2. Crear app:

```bash
heroku create sumaqspa
```

#### 3. Agregar MySQL:

```bash
heroku addons:create cleardb:ignite
```

#### 4. Desplegar:

```bash
git push heroku master
```

---

## 🔧 Configuración

### Variables de Entorno (Producción):

```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:mysql://...
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...

# JWT
JWT_SECRET=...

# Perfil
SPRING_PROFILES_ACTIVE=prod
```

---

## 📚 Documentación

- **Guía de Usuario:** `GUIA_DE_USO.md`
- **Guía de Producción:** `GUIA_PRODUCCION.md`
- **Checklist de Despliegue:** `CHECKLIST_PRODUCCION.md`
- **Sistema Completo:** `SISTEMA_COMPLETO.md`

---

## 🔒 Seguridad

### Contraseñas:
- ✅ Encriptadas con BCrypt (costo 10)
- ✅ Validación de fortaleza en frontend
- ✅ No se almacenan en texto plano

### Autenticación:
- ✅ JWT tokens con expiración
- ✅ Refresh tokens disponibles
- ✅ Validación en cada petición

### Autorización:
- ✅ Roles de usuario (ADMIN/USER)
- ✅ Endpoints protegidos por rol
- ✅ Verificación en frontend y backend

### Datos:
- ✅ Validación de entrada
- ✅ Protección contra SQL injection
- ✅ Sanitización de datos

---

## 🧪 Tests

### Ejecutar tests:

```cmd
mvnw.cmd test
```

### Cobertura:

- Autenticación y autorización
- CRUD de reservas
- Validación de horarios
- Servicios de usuario

---

## 📊 API Endpoints

### Autenticación:

```
POST /api/auth/login         - Iniciar sesión
POST /api/auth/register      - Registrar usuario
```

### Reservas:

```
GET    /api/reservas                    - Listar todas las reservas
POST   /api/reservas                    - Crear reserva
PUT    /api/reservas/update/{id}        - Actualizar reserva (ADMIN)
DELETE /api/reservas/delete/{id}        - Eliminar reserva (ADMIN)
GET    /api/reservas/horarios/{fecha}   - Consultar horarios disponibles
```

---

## 🎨 Personalización

### Colores:

Edita `src/main/resources/static/css/style.css`:

```css
/* Color principal del spa */
--primary-color: #b78e5e;

/* Color de acento */
--accent-color: #28a745;
```

### Logo:

Reemplaza `src/main/resources/static/images/logo.png`

### Textos:

Edita los archivos HTML en `src/main/resources/static/`

---

## 📞 Soporte

**Autor:** Tu Nombre  
**Email:** tu@email.com  
**Website:** https://www.sumaqspa.com  

---

## 📄 Licencia

Este proyecto es de uso privado para SUMAQ SPA.

---

## 🎉 Versión

**Versión:** 1.0.0  
**Fecha:** Noviembre 2025  
**Estado:** ✅ Producción Ready  

---

## 🚀 Siguientes Pasos

Después de desplegar:

1. Accede a tu aplicación
2. Cambia la contraseña del admin
3. Prueba todas las funcionalidades
4. Configura backups automáticos
5. Monitorea los logs
6. ¡Disfruta tu sistema!

---

**¡Gracias por usar SUMAQ SPA!** 🌿✨

