package com.example.integradora5d.security;

import com.example.integradora5d.models.usuario.BeanUsuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private static final String ROLES_CLAIM = "roles";
    private final String secret;
    private final long expirationMs;
    private volatile SecretKey signingKey;
    public JwtService(

            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }
}



//@Service
//public class JwtService {
//
//    private final String SECRET = "mi_clave_secreta";
//
//    public String generateToken(BeanUsuario user) {
//        return Jwts.builder()
//                .setSubject(user.getCorreo())
//                .claim("rol", user.getRol().getNombre())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día
//                .signWith(SignatureAlgorithm.HS256, SECRET)
//                .compact();
//    }
//}
