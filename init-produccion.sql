-- ========================================
-- SUMAQ SPA - Inicializacion de Base de Datos para PRODUCCION
-- ========================================

-- Crear base de datos de produccion
CREATE DATABASE IF NOT EXISTS sumaq_spa_prod
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sumaq_spa_prod;

-- ========================================
-- TABLA: usuarios
-- ========================================

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL COMMENT 'Nombre completo del usuario',
    username VARCHAR(255) UNIQUE NOT NULL COMMENT 'Usuario para login',
    password VARCHAR(255) NOT NULL COMMENT 'Contraseña encriptada con BCrypt',
    role VARCHAR(50) NOT NULL COMMENT 'ADMIN o USER',
    email VARCHAR(255) NOT NULL COMMENT 'Email del usuario',
    telefono VARCHAR(50) NOT NULL COMMENT 'Teléfono de contacto',
    active BOOLEAN DEFAULT TRUE NOT NULL COMMENT 'Usuario activo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Fecha de creación',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABLA: reservas
-- ========================================

CREATE TABLE IF NOT EXISTS reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL COMMENT 'Nombre del cliente',
    correo VARCHAR(255) NOT NULL COMMENT 'Email del cliente',
    telefono VARCHAR(50) NOT NULL COMMENT 'Teléfono del cliente',
    tratamiento VARCHAR(500) NOT NULL COMMENT 'Tratamientos seleccionados',
    total DOUBLE NOT NULL COMMENT 'Precio total',
    fecha DATE NOT NULL COMMENT 'Fecha de la reserva',
    hora TIME NOT NULL COMMENT 'Hora de la reserva',
    usuario_id BIGINT NULL COMMENT 'ID del usuario que hizo la reserva',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de creación',
    CONSTRAINT unique_fecha_hora UNIQUE (fecha, hora),
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_fecha (fecha),
    INDEX idx_usuario (usuario_id),
    INDEX idx_fecha_hora (fecha, hora)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- VERIFICACION
-- ========================================

SELECT 'Tablas creadas:' AS Info;
SHOW TABLES;

SELECT 'Estructura de usuarios:' AS Info;
DESCRIBE usuarios;

SELECT 'Estructura de reservas:' AS Info;
DESCRIBE reservas;

-- ========================================
-- NOTA IMPORTANTE
-- ========================================
-- El usuario administrador se creará automáticamente
-- al iniciar la aplicación Spring Boot por primera vez.
--
-- Credenciales por defecto:
--   Usuario: admin
--   Password: admin123
--
-- ¡IMPORTANTE! Cambia la contraseña del admin
-- después del primer login en producción.
-- ========================================

SELECT '✅ Base de datos de producción inicializada correctamente' AS Resultado;

