package com.nailsbyliz.reservation.config.authtoken;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    static final long EXPIRATIONTIME = 1000 * 60 * 60 * 24;
    static final long AUTH_TOKEN_EXPIRATION = 1000 * 60 * 10; // 10 minutes
    static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours
    static final String PREFIX = "Bearer";
    static final String secretKey = System.getenv("JWT_SECRET_KEY");

    private final Key key;

    @Autowired
    private AppUserRepository repository;

    public JwtService() {
        // this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
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

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(key)
                .compact();
    }

    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String newAuthToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(System.currentTimeMillis() + AUTH_TOKEN_EXPIRATION))
                    .signWith(key)
                    .compact();

            String newAccessToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                    .signWith(key)
                    .compact();

            return Jwts.builder()
                    .claim("authToken", newAuthToken)
                    .claim("accessToken", newAccessToken)
                    .compact();
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired and cannot be refreshed", e);
            throw new BadCredentialsException("Token has expired and cannot be refreshed", e);
        } catch (Exception e) {
            logger.error("Failed to refresh token", e);
            throw new BadCredentialsException("Failed to refresh token", e);
        }
    }

    public UsernamePasswordAuthenticationToken getAuthUser(HttpServletRequest request) {
        String token = resolveToken(request);
        try {
            if (token != null) {
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

    public Long getIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Integer userIdObject = (Integer) claims.get("id");
        if (userIdObject != null) {
            return userIdObject.longValue();
        }
        throw new BadCredentialsException("User ID not found in token");
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(60) // Allow 60 seconds of clock skew
                .build()
                .parseClaimsJws(token.replace(PREFIX, "").trim())
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).setAllowedClockSkewSeconds(1).build()
                    .parseClaimsJws(token.replace(PREFIX, "").trim());
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
