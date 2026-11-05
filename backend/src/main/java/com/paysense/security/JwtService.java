package com.paysense.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key; private final long expirationSeconds;
    public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-seconds:3600}") long expirationSeconds){if(secret.getBytes(StandardCharsets.UTF_8).length<32)throw new IllegalStateException("APP_JWT_SECRET must contain at least 32 characters.");this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expirationSeconds=expirationSeconds;}
    public String issue(UUID userId,String email){Instant now=Instant.now();return Jwts.builder().subject(userId.toString()).claim("email",email).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expirationSeconds))).signWith(key).compact();}
    public UUID subject(String token){return UUID.fromString(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());}
}
