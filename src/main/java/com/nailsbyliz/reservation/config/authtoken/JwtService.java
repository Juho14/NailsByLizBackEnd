package com.nailsbyliz.reservation.config.authtoken;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.repositories.AppUserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtService {

    static final long EXPIRATIONTIME = 1000 * 60 * 60 * 24;
    static final String PREFIX = "Bearer";
    String secretKey = System.getenv("SECRET_KEY");
    // static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Key
    Key key;

    @Autowired
    private AppUserRepository repository;

    public JwtService() {
        // Initialize the key once
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generates a signed JWT token
    public String getToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getId());
        claims.put("fname", userDetails.getFname());
        claims.put("lname", userDetails.getLname());
        claims.put("phone", userDetails.getPhone());
        claims.put("email", userDetails.getEmail());
        claims.put("address", userDetails.getAddress());
        claims.put("postalcode", userDetails.getPostalcode());
        claims.put("city", userDetails.getCity());
        claims.put("role", userDetails.getRole());
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
        String token = resolveToken(request);
        try {
            if (token != null) {
                Claims claims = getClaimsFromToken(token);

                // Directly retrieve the ID as Long
                Long userId = claims.get("id", Long.class);
                if (userId != null) {
                    // Fetch user details from the database using the user_id
                    AppUserEntity userEntity = repository.findById(userId)
                            .orElseThrow(() -> new BadCredentialsException("User not found"));

                    CustomUserDetails userDetails = new CustomUserDetails(
                            userEntity.getUsername(),
                            userEntity.getPasswordHash(),
                            AuthorityUtils.createAuthorityList(userEntity.getRole()),
                            userEntity.getId(),
                            userEntity.getFName(),
                            userEntity.getLName(),
                            userEntity.getPhone(),
                            userEntity.getEmail(),
                            userEntity.getAddress(),
                            userEntity.getPostalcode(),
                            userEntity.getCity(),
                            userEntity.getRole());

                    return new CustomAuthToken(userDetails, null, userDetails.getAuthorities(), userId);
                } else {
                    System.out.println("User ID not found in claims");
                }
            }
            return null;
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    // Method to extract the user ID from the token
    public Long getIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Integer userIdObject = (Integer) claims.get("id");
        if (userIdObject != null) {
            return userIdObject.longValue();
        }
        throw new BadCredentialsException("User ID not found in token");
    }

    // Utility method to parse claims from token
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace(PREFIX, ""))
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            // Parse the token and verify its signature
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.replace(PREFIX, ""));
            // Check if the token has expired
            return true;
        } catch (Exception e) {
            // Token validation failed
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith(PREFIX)) {
            return token.substring(PREFIX.length()).trim();
        }
        return null;
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get("role");
    }

}