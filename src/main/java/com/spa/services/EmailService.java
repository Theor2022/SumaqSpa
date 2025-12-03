package com.spa.services;

import com.spa.model.Reserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Servicio para envío de notificaciones por correo electrónico
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@sumaqspa.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    /**
     * Envía confirmación de reserva por email de forma asíncrona
     */
    @Async
    public void enviarConfirmacionReserva(Reserva reserva) {
        if (!emailEnabled || mailSender == null) {
            logger.info("Email deshabilitado o no configurado. Simulando envío para: {}", reserva.getCorreo());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(reserva.getCorreo());
            message.setSubject("✅ Confirmación de Reserva - SUMAQ SPA");
            message.setText(construirMensajeConfirmacion(reserva));

            mailSender.send(message);
            logger.info("Email de confirmación enviado a: {}", reserva.getCorreo());
        } catch (Exception e) {
            logger.error("Error al enviar email de confirmación: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía notificación de cancelación
     */
    @Async
    public void enviarNotificacionCancelacion(Reserva reserva) {
        if (!emailEnabled || mailSender == null) {
            logger.info("Email deshabilitado. Simulando envío de cancelación para: {}", reserva.getCorreo());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(reserva.getCorreo());
            message.setSubject("❌ Cancelación de Reserva - SUMAQ SPA");
            message.setText(construirMensajeCancelacion(reserva));

            mailSender.send(message);
            logger.info("Email de cancelación enviado a: {}", reserva.getCorreo());
        } catch (Exception e) {
            logger.error("Error al enviar email de cancelación: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía recordatorio 24 horas antes
     */
    @Async
    public void enviarRecordatorio(Reserva reserva) {
        if (!emailEnabled || mailSender == null) {
            logger.info("Email deshabilitado. Simulando recordatorio para: {}", reserva.getCorreo());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(reserva.getCorreo());
            message.setSubject("⏰ Recordatorio de Reserva - SUMAQ SPA");
            message.setText(construirMensajeRecordatorio(reserva));

            mailSender.send(message);
            logger.info("Recordatorio enviado a: {}", reserva.getCorreo());
        } catch (Exception e) {
            logger.error("Error al enviar recordatorio: {}", e.getMessage(), e);
        }
    }

    private String construirMensajeConfirmacion(Reserva reserva) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return String.format("""
            ¡Hola %s!
            
            Tu reserva en SUMAQ SPA ha sido confirmada exitosamente.
            
            📅 Fecha: %s
            🕐 Hora: %s
            💆 Tratamiento(s): %s
            💵 Total: $%.2f
            
            Por favor, llega 10 minutos antes de tu cita.
            
            Política de cancelación:
            - Puedes cancelar hasta 48 horas antes sin cargo
            - Cancelaciones con menos de 48 horas de anticipación pierden el depósito
            
            📍 Dirección: Viamonte 4134, Chacras de Coria
            📞 Teléfono: +54 9 261 466-7433
            
            ¡Te esperamos!
            
            --
            SUMAQ SPA
            Tu bienestar, nuestra prioridad
            """,
                reserva.getNombre(),
                reserva.getFecha().format(dateFormatter),
                reserva.getHora().format(timeFormatter),
                reserva.getTratamientosNombres(),
                reserva.getTotal()
        );
    }

    private String construirMensajeCancelacion(Reserva reserva) {
        return String.format("""
            Hola %s,
            
            Tu reserva para el %s a las %s ha sido cancelada.
            
            Si deseas hacer una nueva reserva, visita nuestra página web.
            
            ¡Esperamos verte pronto!
            
            --
            SUMAQ SPA
            """,
                reserva.getNombre(),
                reserva.getFecha(),
                reserva.getHora()
        );
    }

    private String construirMensajeRecordatorio(Reserva reserva) {
        return String.format("""
            Hola %s,
            
            Te recordamos tu cita en SUMAQ SPA mañana:
            
            📅 %s a las %s
            💆 %s
            
            Recuerda llegar 10 minutos antes.
            
            ¡Te esperamos!
            
            --
            SUMAQ SPA
            """,
                reserva.getNombre(),
                reserva.getFecha(),
                reserva.getHora(),
                reserva.getTratamientosNombres()
        );
    }
}