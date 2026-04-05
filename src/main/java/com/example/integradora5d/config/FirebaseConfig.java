package com.example.integradora5d.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
@ConditionalOnResource(resources = "classpath:firebase-service-account.json")
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try (InputStream serviceAccount =
                     getClass().getClassLoader().getResourceAsStream("firebase-service-account.json")) {
            if (serviceAccount == null) {
                return;
            }
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new RuntimeException("Error al inicializar Firebase: " + detail, e);
        }
    }
}