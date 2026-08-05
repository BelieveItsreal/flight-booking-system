package org.userservice.config;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.userservice.enums.Role;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Long userId, String email, Role role){
        return Jwts.builder()
            .subject(email)
            .claim("uid", userId)
            .claim("role", role.name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis()+jwtProperties.getExpiration()))
            .signWith(getSigingKey(), Jwts.SIG.HS256)
            .compact();
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token){
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Long extractUserId(String token){
        return extractClaim(token, claims -> claims.get("uid", Long.class));
    }

    public boolean isTokenValid(String token, String email){
        return extractEmail(token).equals(email) && !isTokenExpired(token);
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
