package com.example.integradora5d.service.emailsend;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarLinkResetPassword(String correo, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(correo);
            helper.setSubject("Cambio de contraseña");

            String html = """
                <div style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2 style="color: #2c3e50;">Cambio de contraseña</h2>
                    <p>Hola,</p>
                    <p>Haz clic en el siguiente botón para establecer tu contraseña:</p>
                    
                    <a href="%s" 
                       style="display: inline-block; padding: 10px 20px; color: white; 
                              background-color: #3498db; text-decoration: none; border-radius: 5px;">
                        Establecer contraseña
                    </a>

                    <p style="margin-top:20px;">Si no solicitaste esto, ignora este mensaje.</p>
                </div>
            """.formatted(link);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}