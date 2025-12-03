package com.spa.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de cache para mejorar el rendimiento
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configurar el gestor de cache con múltiples caches
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(Arrays.asList(
                // Cache para horarios disponibles (se limpia al crear/modificar reservas)
                new ConcurrentMapCache("horariosDisponibles"),

                // Cache para tratamientos (se limpia al modificar tratamientos)
                new ConcurrentMapCache("tratamientos"),

                // Cache para estadísticas (se limpia periódicamente)
                new ConcurrentMapCache("estadisticas")
        ));

        return cacheManager;
    }
}