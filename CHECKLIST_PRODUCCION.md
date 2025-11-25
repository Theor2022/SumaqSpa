# ✅ CHECKLIST DE DESPLIEGUE A PRODUCCIÓN

## 📋 ANTES DE DESPLEGAR

### Preparación del Proyecto:
- [ ] Todos los errores corregidos
- [ ] Aplicación funciona correctamente en local
- [ ] Tests ejecutados y pasando
- [ ] Código revisado y limpio

### Configuración:
- [ ] `application-prod.properties` configurado
- [ ] JWT secret cambiado a uno seguro
- [ ] URLs de base de datos actualizadas
- [ ] Credenciales de BD configuradas

### Archivos:
- [ ] `.gitignore` creado
- [ ] `.env.example` creado
- [ ] `Dockerfile` creado (si usas Docker)
- [ ] `docker-compose.yml` creado (si usas Docker)

---

## 🔨 BUILD DE PRODUCCIÓN

### Generar JAR:
- [ ] Ejecutado: `build-produccion.bat`
- [ ] JAR creado en: `target\demo-0.0.1-SNAPSHOT.jar`
- [ ] Tamaño del JAR verificado (>20MB típicamente)
- [ ] JAR probado localmente

---

## 🖥️ SERVIDOR (VPS/DEDICADO)

### Preparación del Servidor:
- [ ] Servidor Linux contratado (Ubuntu 22.04)
- [ ] Acceso SSH configurado
- [ ] Java 17+ instalado
- [ ] MySQL 8.0+ instalado
- [ ] Firewall configurado (puerto 8080)

### Base de Datos:
- [ ] Base de datos `sumaq_spa_prod` creada
- [ ] Usuario de BD creado
- [ ] Permisos otorgados
- [ ] Tablas creadas (o se crearán automáticamente)
- [ ] Conexión probada

### Aplicación:
- [ ] Directorio `/opt/sumaqspa` creado
- [ ] Usuario del sistema `sumaqspa` creado
- [ ] JAR subido al servidor
- [ ] Servicio systemd configurado
- [ ] Servicio habilitado e iniciado
- [ ] Logs verificados sin errores

### Red:
- [ ] Puerto 8080 abierto en firewall
- [ ] Aplicación accesible desde navegador
- [ ] Nginx instalado (si usas proxy reverso)
- [ ] Dominio apuntando al servidor
- [ ] SSL/HTTPS configurado (Let's Encrypt)

---

## 🐳 DOCKER

### Preparación:
- [ ] Docker instalado
- [ ] Docker Compose instalado
- [ ] Archivo `.env` creado con valores reales
- [ ] Puertos disponibles (8080, 3307)

### Despliegue:
- [ ] Ejecutado: `docker-compose up -d --build`
- [ ] Contenedor MySQL corriendo
- [ ] Contenedor App corriendo
- [ ] Logs verificados sin errores
- [ ] Aplicación accesible

---

## ☁️ CLOUD (Heroku/Railway/etc.)

### Preparación:
- [ ] Cuenta creada en la plataforma
- [ ] CLI instalado (si aplica)
- [ ] Repositorio Git inicializado
- [ ] `Procfile` creado (Heroku)
- [ ] Variables de entorno configuradas

### Despliegue:
- [ ] Código subido a GitHub
- [ ] Proyecto conectado a la plataforma
- [ ] Base de datos agregada
- [ ] Variables de entorno configuradas
- [ ] Deploy ejecutado
- [ ] Aplicación accesible en la URL generada

---

## 🔐 SEGURIDAD

### Contraseñas:
- [ ] Contraseña del admin cambiada
- [ ] Password de BD es seguro (>12 caracteres)
- [ ] JWT secret es único y largo (>64 caracteres)
- [ ] No hay contraseñas en el código fuente
- [ ] Variables de entorno usadas correctamente

### Configuración:
- [ ] `spring.jpa.show-sql=false` en producción
- [ ] Logs NO muestran datos sensibles
- [ ] Error messages deshabilitados en producción
- [ ] CORS configurado correctamente
- [ ] HTTPS habilitado

---

## 📊 MONITOREO

### Logs:
- [ ] Logs configurados correctamente
- [ ] Rotación de logs habilitada
- [ ] Puedo acceder a los logs fácilmente

### Backups:
- [ ] Script de backup de BD creado
- [ ] Backup automático configurado (cron)
- [ ] Probado restaurar un backup
- [ ] Backups guardados fuera del servidor

### Rendimiento:
- [ ] Aplicación responde en <2 segundos
- [ ] Base de datos indexada correctamente
- [ ] Pool de conexiones configurado
- [ ] Memoria suficiente (mínimo 2GB RAM)

---

## 🧪 VERIFICACIÓN FINAL

### Tests Funcionales:
- [ ] Página de inicio carga correctamente
- [ ] Login funciona
- [ ] Registro funciona
- [ ] Carrito funciona
- [ ] Crear reserva funciona
- [ ] Panel admin funciona
- [ ] Editar reserva funciona
- [ ] Eliminar reserva funciona
- [ ] Cerrar sesión funciona
- [ ] Botón login aparece/desaparece correctamente

### Tests de Seguridad:
- [ ] Usuario normal NO puede acceder al panel admin
- [ ] Sin token NO se pueden hacer reservas
- [ ] URLs directas protegidas
- [ ] SQL injection probado (debe fallar)
- [ ] XSS probado (debe fallar)

### Tests de Rendimiento:
- [ ] Página carga en <3 segundos
- [ ] Múltiples usuarios simultáneos
- [ ] Base de datos responde rápido
- [ ] No hay memory leaks

---

## 🎉 DESPLIEGUE COMPLETADO

Una vez que todos los checkboxes estén marcados:

- ✅ Tu aplicación está en PRODUCCIÓN
- ✅ Es accesible públicamente
- ✅ Es segura
- ✅ Tiene backups
- ✅ Está monitoreada

---

## 📞 SOPORTE

### Si algo falla:

1. **Revisa los logs:**
   ```bash
   tail -f /opt/sumaqspa/logs/app.log
   ```

2. **Verifica el servicio:**
   ```bash
   sudo systemctl status sumaqspa
   ```

3. **Verifica la conexión a BD:**
   ```bash
   mysql -u sumaqspa -p sumaq_spa_prod
   ```

4. **Verifica el puerto:**
   ```bash
   sudo netstat -tulpn | grep 8080
   ```

---

## 🎯 SIGUIENTES PASOS RECOMENDADOS

Después de desplegar:

1. [ ] Configurar monitoreo (UptimeRobot, Pingdom)
2. [ ] Configurar alertas por email
3. [ ] Implementar logs centralizados
4. [ ] Configurar CDN para imágenes (CloudFlare)
5. [ ] Implementar analytics (Google Analytics)
6. [ ] Crear documentación de API
7. [ ] Configurar CI/CD (GitHub Actions)

---

**¡Tu aplicación está lista para el mundo real!** 🌍🚀

