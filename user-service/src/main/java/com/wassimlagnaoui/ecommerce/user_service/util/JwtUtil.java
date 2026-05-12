package com.wassimlagnaoui.ecommerce.user_service.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        // convert the secret string into a SecretKey using Keys.hmacShaKeyFor()
        // hint: secret.getBytes() gives you the bytes
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return secretKey;
    }

    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("userId",userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSigningKey())
                .compact();
        // use Jwts.builder()
        // set subject to email
        // add claim "userId" with the userId value
        // set issuedAt to now
        // set expiration to now + expiration milliseconds
        // sign with getSigningKey()
        // call .compact() at the end to get the String
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        // use Jwts.parser()
        // set the signing key
        // parse the token
        // return the payload (claims)
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        }catch (Exception e){
            log.error("Excpetion Error message: "+ e.getMessage());
            return false;
        }

    }
}