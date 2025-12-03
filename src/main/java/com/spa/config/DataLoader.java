package com.spa.config;

import com.spa.model.Tratamiento;
import com.spa.model.Usuario;
import com.spa.repository.TratamientoRepository;
import com.spa.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Carga datos iniciales en la base de datos
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final TratamientoRepository tratamientoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository,
                      TratamientoRepository tratamientoRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear usuarios por defecto
        crearUsuariosDefecto();

        // Crear tratamientos por defecto
        crearTratamientosDefecto();
    }

    private void crearUsuariosDefecto() {
        // Usuario administrador
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEmail("admin@sumaqspa.com");
            admin.setTelefono("0000000000");
            admin.setActive(true);
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin / admin123");
        }

        // Usuario de prueba
        if (usuarioRepository.findByUsername("user").isEmpty()) {
            Usuario user = new Usuario();
            user.setNombre("Usuario de Prueba");
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            user.setEmail("user@example.com");
            user.setTelefono("1111111111");
            user.setActive(true);
            usuarioRepository.save(user);
            System.out.println("✅ Usuario de prueba creado: user / user123");
        }
    }

    private void crearTratamientosDefecto() {
        if (tratamientoRepository.count() == 0) {
            // Masajes
            Tratamiento masajeRelajante = new Tratamiento(
                    "Masaje Relajante",
                    "Masaje de cuerpo completo que utiliza maniobras de relajación progresiva para llegar a una mayor profundidad, aflojando las contracturas musculares y proporcionando un estado de armonía.",
                    60,
                    new BigDecimal("5000")
            );
            masajeRelajante.setCategoria("Masajes");
            masajeRelajante.setImagenUrl("/images/masaje.jpg");
            tratamientoRepository.save(masajeRelajante);

            Tratamiento masajeDescontracturante = new Tratamiento(
                    "Masaje Descontracturante",
                    "Masaje profundo para aliviar contracturas y mejorar la circulación con aceites esenciales y técnicas específicas.",
                    90,
                    new BigDecimal("6500")
            );
            masajeDescontracturante.setCategoria("Masajes");
            masajeDescontracturante.setImagenUrl("/images/optimizada_2.webp");
            tratamientoRepository.save(masajeDescontracturante);

            Tratamiento masajeAyurvedico = new Tratamiento(
                    "Masaje Ayurvédico",
                    "Masaje tradicional de la India que armoniza el cuerpo y la mente, relajante y desintoxicante.",
                    30,
                    new BigDecimal("3500")
            );
            masajeAyurvedico.setCategoria("Masajes");
            masajeAyurvedico.setImagenUrl("/images/images (1).jpg");
            tratamientoRepository.save(masajeAyurvedico);

            Tratamiento masajePindas = new Tratamiento(
                    "Masaje con Pindas",
                    "Masaje con pindas herbales calientes para equilibrar el cuerpo y promover el bienestar.",
                    70,
                    new BigDecimal("5500")
            );
            masajePindas.setCategoria("Masajes");
            masajePindas.setImagenUrl("/images/optimizada_3.webp");
            tratamientoRepository.save(masajePindas);

            Tratamiento masajeVibroAcustico = new Tratamiento(
                    "Masaje Vibro-Acústico",
                    "Integración de técnicas manuales con vibraciones acústicas mediante cuenco especializado.",
                    95,
                    new BigDecimal("8000")
            );
            masajeVibroAcustico.setCategoria("Masajes");
            masajeVibroAcustico.setImagenUrl("/images/optimizada_4.webp");
            tratamientoRepository.save(masajeVibroAcustico);

            // Tratamientos corporales
            Tratamiento exfoliacion = new Tratamiento(
                    "Exfoliación Corporal",
                    "Tratamiento ayurvédico para renovar la piel y estimular la circulación con plantas nativas.",
                    45,
                    new BigDecimal("4500")
            );
            exfoliacion.setCategoria("Corporales");
            exfoliacion.setImagenUrl("/images/optimizada_5.webp");
            tratamientoRepository.save(exfoliacion);

            Tratamiento mascarillaCorporal = new Tratamiento(
                    "Mascarilla Corporal",
                    "Aplicación de cremas naturales, arcillas y aceites para hidratar y revitalizar la piel.",
                    65,
                    new BigDecimal("6500")
            );
            mascarillaCorporal.setCategoria("Corporales");
            mascarillaCorporal.setImagenUrl("/images/macacorp.png");
            tratamientoRepository.save(mascarillaCorporal);

            System.out.println("✅ Tratamientos por defecto creados: " + tratamientoRepository.count());
        }
    }
}