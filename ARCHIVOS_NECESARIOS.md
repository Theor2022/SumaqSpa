# 📁 ARCHIVOS NECESARIOS vs NO NECESARIOS

## ✅ ARCHIVOS CRÍTICOS (NO BORRAR NUNCA)

### Backend (Java):
```
✅ pom.xml                           - Dependencias Maven
✅ mvnw                              - Maven Wrapper (Linux)
✅ mvnw.cmd                          - Maven Wrapper (Windows)
✅ .mvn/                             - Config Maven Wrapper
✅ src/main/java/                    - TODO el código Java
✅ src/main/resources/               - Configuraciones y frontend
   ✅ application.properties
   ✅ application-dev.properties
   ✅ application-prod.properties
   ✅ static/                        - HTML, CSS, JS, imágenes
```

---

## 🟢 ARCHIVOS ÚTILES (MANTENER)

### Para Producción:
```
✅ Dockerfile                        - Imagen Docker
✅ docker-compose.yml                - Docker + MySQL
✅ Procfile                          - Heroku/Railway
✅ .env.example                      - Template variables
✅ .gitignore                        - Git
```

### Para Despliegue Linux:
```
✅ deploy.sh                         - Script despliegue
✅ stop.sh                           - Detener app
✅ sumaqspa.service                  - Servicio systemd
✅ init-produccion.sql               - BD producción
```

### Documentación:
```
✅ README.md                         - Doc principal
✅ GUIA_PRODUCCION.md                - Guía completa
✅ DESPLIEGUE_RAPIDO.md              - Guía rápida
✅ CHECKLIST_PRODUCCION.md           - Checklist
✅ GUIA_DE_USO.md                    - Para usuarios
```

---

## ❌ ARCHIVOS A ELIMINAR

### Scripts .BAT (8 archivos):
```
❌ build-produccion.bat
❌ build-jar.bat
❌ ejecutar-produccion.bat
❌ REINICIAR.bat
❌ PRUEBA_BOTON_LOGIN.bat
❌ MENU.bat
❌ solucionar_403.bat
❌ reinicio_completo.bat
```

### Documentos MD viejos (6-7 archivos):
```
❌ BARRA_USUARIO.md
❌ BOTON_LOGIN_DINAMICO.md
❌ INSTRUCCIONES_FINALES.md
❌ REGISTRO_COMPLETO.md
❌ SOLUCION_ERROR_500.md
❌ SOLUCION_LOMBOK.md
❌ SISTEMA_COMPLETO.md (opcional)
```

### SQL Scripts viejos (5 archivos):
```
❌ agregar_campo_nombre.sql
❌ fix_created_at.sql
❌ limpiar_bd.sql
❌ recrear_tabla_usuarios.sql
❌ verificar_bd.sql
```

### Carpetas auto-generadas:
```
❌ target/                           - Se regenera con Maven
❌ .idea/                            - IDE, se regenera
```

---

## 🔄 COMANDOS SIN .BAT

### En lugar de los .bat, usa directamente:

**Iniciar desarrollo:**
```cmd
mvnw.cmd spring-boot:run
```

**Generar JAR producción:**
```cmd
mvnw.cmd clean package -DskipTests
```

**Ejecutar JAR:**
```cmd
java -jar target\demo-0.0.1-SNAPSHOT.jar
```

**Con Docker:**
```cmd
docker-compose up -d --build
```

**Limpiar proyecto:**
```cmd
mvnw.cmd clean
```

---

## 📊 RESUMEN EJECUTIVO

### MANTENER (33 archivos esenciales):
- Código Java: 19 archivos
- Frontend: 8+ HTML + CSS + JS
- Config: 3 properties
- Docker: 2 archivos
- Docs: 5 archivos .md

### ELIMINAR (19+ archivos):
- Scripts .bat: 8 archivos
- Docs viejos: 6-7 archivos
- SQL viejos: 5 archivos

---

## ✅ ESTRUCTURA FINAL LIMPIA

```
SumaqSpa/
├── .mvn/                    ✅
├── src/                     ✅
├── .env.example             ✅
├── .gitignore               ✅
├── Dockerfile               ✅
├── docker-compose.yml       ✅
├── Procfile                 ✅
├── deploy.sh                ✅
├── stop.sh                  ✅
├── sumaqspa.service         ✅
├── init-produccion.sql      ✅
├── mvnw                     ✅
├── mvnw.cmd                 ✅
├── pom.xml                  ✅
├── README.md                ✅
├── GUIA_PRODUCCION.md       ✅
├── DESPLIEGUE_RAPIDO.md     ✅
├── CHECKLIST_PRODUCCION.md  ✅
└── GUIA_DE_USO.md           ✅
```

**Total: ~20 archivos en raíz + código fuente**

---

## 🎯 ¿ELIMINO LOS ARCHIVOS NO NECESARIOS?

**Puedo eliminar:**
- ❌ 8 archivos .bat
- ❌ 6 archivos .md viejos
- ❌ 5 archivos .sql viejos

**Total a eliminar:** 19 archivos

**¿Procedo a eliminarlos?**

