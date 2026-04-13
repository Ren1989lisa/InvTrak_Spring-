package com.example.integradora5d.service.emailsend;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void enviarLinkResetPassword(String correo, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(correo);
            helper.setSubject("Cambio de contraseña");

            String html = """
                    <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #d1d9e0; border-radius: 8px; overflow: hidden;">
                        <div style="background-color: #1a5276; padding: 20px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px;">Cambio de contraseña</h1>
                        </div>
                    
                        <div style="padding: 30px; background-color: #ffffff;">
                            <p style="color: #333; font-size: 16px;">Hola,</p>
                            <p style="color: #555; line-height: 1.6;">
                                Has recibido este mensaje desde el sistema <b>InvTrack</b>.
                                Para cambiar tu contraseña debes dar click en el botón.
                            </p>
                    
                            <div style="text-align: center; margin: 30px 0;">
                                <a href="%s"
                                   style="display: inline-block; padding: 12px 25px; color: #ffffff;
                                              background-color: #2980b9; text-decoration: none; border-radius: 5px;
                                              font-weight: bold; font-size: 16px;">
                                    Cambiar Contraseña
                                </a>
                            </div>
                    
                            <div style="background-color: #eaf2f8; padding: 15px; border-left: 4px solid #2980b9; border-radius: 4px;">
                                <p style="margin: 0; color: #2c3e50; font-size: 14px;">
                                    <b>Atención:</b> Este enlace es por seguridad y <b>expirará en 15 minutos</b>.
                                    Si el tiempo se agota, deberás solicitar un nuevo correo de recuperación.
                                </p>
                            </div>
                    
                            <p style="color: #777; font-size: 14px; margin-top: 20px;">
                                Si no solicitaste esta cuenta, puedes ignorar este mensaje de forma segura.
                            </p>
                        </div>
                    
                        <div style="background-color: #f4f6f7; padding: 15px; text-align: center; color: #999; font-size: 12px;">
                            &copy; 2026 InvTrack. Todos los derechos reservados.
                        </div>
                    </div>
            """.formatted(link);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Error enviando el correo: " + e.getMessage());
        }
    }
}