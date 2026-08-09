package com.gathera.gathera.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {

    private final SecretKey key;

    private final long exp_time;

    public JwtTokenManager(
            @Value("${jwt.secret-key}") String key,
            @Value("${jwt.lifetime}") long exp_time
    ) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
        this.exp_time = exp_time;
    }

    // подумать над дополнением данных, которые заполняются по пользователю
    public String generateJwtToken(String login){
        return Jwts
                .builder()
                .subject(login)
                .signWith(key)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + exp_time))
                .compact();
    }

    public String getLoginFromToken(String jwt){
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
