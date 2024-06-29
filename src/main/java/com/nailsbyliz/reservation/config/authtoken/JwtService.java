package com.nailsbyliz.reservation.config.authtoken;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.repositories.AppUserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    static final long AUTH_TOKEN_EXPIRATION = 1000 * 60 * 10; // 10 minutes
    static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours
    static final String PREFIX = "Bearer";
    static final String secretKey = System.getenv("JWT_SECRET_KEY");
    // static final String secretKey =
    // "Renatemeoweeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeelöaksjdflajflk";
    private final Key key;

    @Autowired
    private AppUserRepository repository;

    public JwtService() {
        // this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public UsernamePasswordAuthenticationToken getAuthUser(HttpServletRequest request) {
        String token = resolveAccessToken(request);
        try {
            if (token != null && validateToken(token)) {
                Claims claims = getClaimsFromToken(token);
                Long userId = claims.get("id", Long.class);
                if (userId != null) {
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
                    logger.error("User ID not found in claims");
                }
            }
            return null;
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired", e);
            throw new ExpiredJwtException(null, null, token);
        } catch (Exception e) {
            logger.error("Invalid token", e);
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    public String generateAuthToken(CustomUserDetails userDetails) {
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

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + AUTH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getId());
        claims.put("role", userDetails.getRole());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        if (token != null && token.startsWith(PREFIX)) {
            return token.substring(PREFIX.length()).trim();
        }
        return null;
    }

    public String resolveAuthToken(HttpServletRequest request) {
        String token = request.getHeader("Auth-Token");
        if (token != null && token.startsWith(PREFIX)) {
            return token.substring(PREFIX.length()).trim();
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).setAllowedClockSkewSeconds(60).build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid token format", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported token", e);
        } catch (SignatureException e) {
            logger.error("Invalid token signature", e);
        } catch (IllegalArgumentException e) {
            logger.error("Token is empty or null", e);
        } catch (Exception e) {
            logger.error("Token validation failed", e);
        }
        return false;
    }

    public Long getIdFromAccessToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("id", Long.class);
    }

    public Long getIdFromAuthToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("id", Long.class);
    }

    public String getRoleFromAccessToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public String getRoleFromAuthToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token.replace(PREFIX, "").trim())
                .getBody();
    }
}