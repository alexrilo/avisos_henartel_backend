package com.serviflow.auth.infrastructure.security;

import com.serviflow.auth.domain.model.AuthToken;
import com.serviflow.auth.domain.port.TokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter implementing the domain TokenProvider port using JJWT 0.12.x+.
 */
public class JwtTokenProviderAdapter implements TokenProvider {

    private final byte[] secretKey;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProviderAdapter(String secret, long expirationMs, long refreshExpirationMs) {
        this.secretKey = secret.getBytes();
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    public AuthToken generateToken(String username, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMs);
        
        SecretKey key = Keys.hmacShaKeyFor(secretKey);

        String token = Jwts.builder()
            .subject(username)
            .claims(claims)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(key)
            .compact();

        return AuthToken.of(token, expiresAt);
    }

    @Override
    public AuthToken generateRefreshToken(String username, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(refreshExpirationMs);
        
        SecretKey key = Keys.hmacShaKeyFor(secretKey);

        String token = Jwts.builder()
            .subject(username)
            .claims(claims)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(key)
            .compact();

        return AuthToken.of(token, expiresAt);
    }

    @Override
    public String extractUsername(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey);
        
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    @Override
    public Map<String, Object> extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey);
        
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        
        return new HashMap<>(claims);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey);
            
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
