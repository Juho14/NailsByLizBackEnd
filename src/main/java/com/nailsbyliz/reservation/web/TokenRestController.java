package com.nailsbyliz.reservation.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.config.authtoken.CustomUserDetails;
import com.nailsbyliz.reservation.config.authtoken.JwtService;
import com.nailsbyliz.reservation.domain.AppUserEntity;
import com.nailsbyliz.reservation.repositories.AppUserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@PreAuthorize("permitAll()")
public class TokenRestController {

    private static final Logger logger = LoggerFactory.getLogger(TokenRestController.class);

    @Autowired
    private JwtEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    AppUserRepository userRepository;

    @PostMapping("/api/public/token")
    public ResponseEntity<?> generateAuthToken(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String authToken = jwtService.generateAuthToken(userDetails); // Generate auth token

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to generate authentication token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/validate")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> validateToken(HttpServletRequest request) {
        String accessToken = jwtService.resolveAccessToken(request);
        Map<String, String> response = new HashMap<>();

        try {
            if (accessToken != null && jwtService.validateToken(accessToken)) {
                response.put("status", "valid");
                response.put("message", "Access token is valid");
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

    @PostMapping("/api/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String accessToken = jwtService.resolveAccessToken(request);
        Map<String, String> response = new HashMap<>();

        try {
            if (accessToken != null && jwtService.validateToken(accessToken)) {
                Long userId = jwtService.getIdFromToken(accessToken); // Extract user ID from token
                Optional<AppUserEntity> userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    AppUserEntity userEntity = userOptional.get();
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

                    String newAuthToken = jwtService.generateAuthToken(userDetails);
                    String newAccessToken = jwtService.generateAccessToken(userDetails);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAuthToken)
                            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION)
                            .body(Map.of(
                                    "authToken", newAuthToken,
                                    "accessToken", newAccessToken));
                } else {
                    response.put("status", "error");
                    response.put("message", "User not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                response.put("status", "error");
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to refresh token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}