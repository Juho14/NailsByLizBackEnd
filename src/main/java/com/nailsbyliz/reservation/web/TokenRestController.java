package com.nailsbyliz.reservation.web;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.config.authtoken.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TokenRestController {

    @Autowired
    JwtEncoder encoder;

    @Autowired
    JwtService jwtService;

    @PostMapping("/api/public/token")
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
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String token = jwtService.resolveToken(request);
            if (token != null && jwtService.validateToken(token)) {
                return ResponseEntity.ok().body("Token is valid");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            // Log the exception and return an appropriate response
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

}