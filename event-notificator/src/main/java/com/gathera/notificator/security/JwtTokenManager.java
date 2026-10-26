package com.gathera.notificator.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {

    private final SecretKey key;

    public JwtTokenManager(
            @Value("${jwt.secret-key}") String key
    ) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }

    public Long getUserIdFromToken(String jwt){
        Object userId = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .get("userId");

        if (userId instanceof Number number){
            return number.longValue();
        }
        return null;
    }

}
