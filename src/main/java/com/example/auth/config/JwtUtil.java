package com.example.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET = "mySuperSecretJwtKey12345678901234567890";
    private final long EXPIRATION_TIME = 1000*60*60*24;

    private Key getSecretKEY(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email, String role){
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                .signWith(getSecretKEY(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSecretKEY())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserEmail(String token){
        return getClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token){
        Date expiration = getClaims(token).getExpiration();
        return expiration.after(new Date());
    }

    public String extractRole(String token){
        Claims claims = getClaims(token);
        return getClaims(token).get("role", String.class);
    }
}
