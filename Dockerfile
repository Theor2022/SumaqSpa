# ========================================
# SUMAQ SPA - Dockerfile para Produccion
# ========================================

# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (cache)
RUN mvn dependency:go-offline -B

# Copiar codigo fuente
COPY src ./src

# Compilar y empaquetar
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR desde etapa de build
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno (se pueden sobreescribir)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# ========================================
# INSTRUCCIONES DE USO
# ========================================
#
# 1. Construir imagen:
#    docker build -t sumaqspa:latest .
#
# 2. Ejecutar contenedor:
#    docker run -d \
#      --name sumaqspa \
#      -p 8080:8080 \
#      -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/sumaq_spa \
#      -e SPRING_DATASOURCE_USERNAME=root \
#      -e SPRING_DATASOURCE_PASSWORD=tu_password \
#      -e JWT_SECRET=tu_secreto_seguro \
#      sumaqspa:latest
#
# 3. Ver logs:
#    docker logs -f sumaqspa
#
# 4. Detener:
#    docker stop sumaqspa
#
# 5. Eliminar:
#    docker rm sumaqspa
#
# ========================================

