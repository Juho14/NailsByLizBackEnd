package com.nailsbyliz.reservation.config.authtoken;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtService {
    static final long EXPIRATIONTIME = 1000 * 60 * 60 * 24;
    static final String PREFIX = "Bearer";
    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Key must be changed in production environment

    // Generates a signed JWT token
    public String getToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getId());
        claims.put("fname", userDetails.getFname());
        claims.put("lname", userDetails.getLname());
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(key)
                .compact();
        return token;
    }

    // Gets a token from request Authorization header, verifies a token, gets user
    // details
    public UsernamePasswordAuthenticationToken getAuthUser(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            if (token != null) {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token.replace(PREFIX, ""))
                        .getBody();
                try {
                    Integer userIdObject = (Integer) claims.get("id");
                    if (userIdObject != null) {
                        long user_id = userIdObject.longValue();
                        CustomAuthToken customtoken = new CustomAuthToken(claims.getSubject(), null,
                                Collections.emptyList(), user_id);
                        return customtoken;
                    } else {
                        System.out.println("User ID not found in claims");
                    }
                } catch (ClassCastException e) {
                    System.out.println("Error casting user_id to Integer: " + e.getMessage());
                }
            }
            return null;
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token");
        }
    }
}