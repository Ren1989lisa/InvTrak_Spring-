package com.example.integradora5d.service.notificacion;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionService {

    @Transactional
    public void enviarNotificacion(String tokenDispositivo, String titulo, String cuerpo) {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build())
                    .setToken(tokenDispositivo)
                    .build();

            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
    }
}
