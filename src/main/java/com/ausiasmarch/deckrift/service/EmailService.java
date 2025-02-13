package com.ausiasmarch.deckrift.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail) {
        String subject = "Verifica tu cuenta";
        String verificationLink = "http://localhost:4200/verify-email?email=" + toEmail;

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
}
