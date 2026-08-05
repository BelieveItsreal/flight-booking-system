package org.flightservice.config;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Verification-only: flight-service trusts tokens issued by user-service
 * (same shared secret) and never issues its own.
 */
@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token){
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = Jwts.parser()
            .verifyWith(getSigingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claimsResolver.apply(claims);
    }

    private SecretKey getSigingKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
}
