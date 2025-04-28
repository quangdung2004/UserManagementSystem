package com.example.usermngsystem.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;

@Component
public class JwtUtils {
    private final String jwtSecret = "mySecretKey123";
    private final long jwtExpirationMs = 86400000; // 1 day

    public String generateJwt(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwt(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateJwt(userPrincipal.getUsername());
    }
}

