package com.ausiasmarch.deckrift.service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    @Value("${jwt.subject}")
    private String SUBJECT;
    @Value("${jwt.issuer}")
    private String ISSUER;
    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Map<String, String> claims) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .claim("correo", claims.get("correo"))  // 📌 Asegurar que se incluye el correo
                .subject(SUBJECT)
                .issuer(ISSUER)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 6000000)) // Expira en 100 minutos
                .signWith(getSecretKey())
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    public String validateToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
        
            if (claims.getExpiration().before(new Date())) {
                return null;
            }
    
            if (!claims.getIssuer().equals(ISSUER)) {
                return null;
            }
    
            if (!claims.getSubject().equals(SUBJECT)) {
                return null;
            }
    
            return claims.get("correo", String.class);
        } catch (Exception e) {
            return null;
        }
    }
    }
