package com.finance.tracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey jwtSecretKey =Keys.hmacShaKeyFor("c2tOJ4uQT1WCSzuNsylJ+aSAe/oFiDUv6tZt82cakqo=".getBytes(StandardCharsets.UTF_8));
    private final long jwtExpirationMs = 864000000; //240 hours

    SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .claim("role", "ROLE_USER")
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256) // ✅ Modern syntax
                .compact();
    }
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecretKey) // ✅ Modern parser
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()// ✅ Validates signature
                ; // ✅ Returns claims
    }
}
