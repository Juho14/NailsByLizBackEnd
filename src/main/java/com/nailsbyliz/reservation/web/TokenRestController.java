package com.nailsbyliz.reservation.web;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.config.authtoken.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TokenRestController {

    private static final Logger logger = LoggerFactory.getLogger(TokenRestController.class);

    @Autowired
    private JwtEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/api/public/token")
    @PreAuthorize("permitAll()")
    public String token(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @GetMapping("/api/public/validate")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> validateToken(HttpServletRequest request) {
        String token = jwtService.resolveToken(request);
        Map<String, String> response = new HashMap<>();
        try {
            boolean isValid = jwtService.validateToken(token);
            if (isValid) {
                response.put("status", "valid");
                response.put("message", "Token is valid");
                return ResponseEntity.ok().body(response);
            } else {
                response.put("status", "unauthorized");
                response.put("message", "Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (ExpiredJwtException e) {
            response.put("status", "expired");
            response.put("message", "Token has expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Token validation failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
