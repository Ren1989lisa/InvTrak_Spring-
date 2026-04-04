package com.example.integradora5d.service.emailsend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// EmailService.java
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarLinkResetPassword(String correo, String link) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(correo);
        msg.setSubject("Activa tu cuenta");
        msg.setText("Establece tu contraseña aquí: " + link);
        mailSender.send(msg);
    }
}