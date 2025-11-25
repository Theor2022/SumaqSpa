# COMANDOS ESENCIALES SIN .BAT

## DESARROLLO
mvnw.cmd spring-boot:run

## PRODUCCION
mvnw.cmd clean package -DskipTests

## EJECUTAR JAR
java -jar target\demo-0.0.1-SNAPSHOT.jar

## DOCKER
docker-compose up -d --build

## LIMPIAR
mvnw.cmd clean

