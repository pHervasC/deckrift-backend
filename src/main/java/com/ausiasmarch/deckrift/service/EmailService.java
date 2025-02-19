package com.ausiasmarch.deckrift.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender, UsuarioRepository usuarioRepository) {
        this.mailSender = mailSender;
        this.usuarioRepository = usuarioRepository; // Se añade la inyección del repositorio
    }

    public void sendVerificationEmail(String toEmail) {
        String subject = "Verifica tu cuenta";
        String verificationLink = "http://localhost:8085/api/auth/verify-email?email=" + toEmail; // Se usa la URL del backend para verificar

        String htmlMessage = "<div style='font-family: Arial, sans-serif; text-align: center;'>" +
                "<h2>¡Bienvenido a DeckRift!</h2>" +
                "<p>Para completar tu registro, haz clic en el siguiente botón:</p>" +
                "<a href='" + verificationLink + "' style='background-color: #007bff; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px;'>Verificar Email</a>" +
                "<p>Si no puedes hacer clic en el botón, copia y pega este enlace en tu navegador:</p>" +
                "<p><a href='" + verificationLink + "'>" + verificationLink + "</a></p>" +
                "</div>";

        try {
            logger.info("🔹 Intentando enviar email a: " + toEmail);

            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            helper.setFrom("phervasc2005@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlMessage, true);

            mailSender.send(mailMessage);

            logger.info("✅ Email enviado a: " + toEmail);
        } catch (MessagingException e) {
            logger.error("❌ Error al enviar el email a " + toEmail, e);
        }
    }

    public ResponseEntity<Void> verifyEmail(String email) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "http://localhost:4200/verified")
                    .build();
        }

        usuario.setEmailVerified(true);
        usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:4200/verified")
                .build();
    }
}
