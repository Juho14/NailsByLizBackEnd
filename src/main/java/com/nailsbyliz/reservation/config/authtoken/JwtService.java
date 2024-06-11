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
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtService {

    static final long EXPIRATIONTIME = 1000 * 60 * 60 * 24;
    // static final long EXPIRATIONTIME = 2000; // 5 seconds for development
    static final String PREFIX = "Bearer";
    Key key;

    @Autowired
    private AppUserRepository repository;

    public JwtService() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
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
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(key)
                .compact();
        return token;
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
                    System.out.println("User ID not found in claims");
                }
            }
            return null;
        } catch (Exception e) {
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
                .build()
                .parseClaimsJws(token.replace(PREFIX, ""))
                .getBody();
    }

    public boolean validateToken(String token) throws Exception {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.replace(PREFIX, ""));
            return true;
        } catch (Exception e) {
            throw e;
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