package com.example.gerenciamento_tickets.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.gerenciamento_tickets.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenJWTService {


    private String secret;

    private final Algorithm ALGORITHM;

    @Value("${jwt.expiresAt}")
    private long EXPIRES_AT_MILLIS;

    public TokenJWTService(@Value("${jwt.secret}") String secret) {
        ALGORITHM = Algorithm.HMAC256(secret);
    }

    public String generateToken(Usuario usuario) {
        return JWT.create()
                .withSubject(usuario.getUsername())
                .withExpiresAt(Instant.now().plusMillis(EXPIRES_AT_MILLIS))
                .sign(ALGORITHM);
    }

    public String validateToken(String token) {
        return JWT.require(ALGORITHM)
                .build()
                .verify(token)
                .getSubject();
    }
}
