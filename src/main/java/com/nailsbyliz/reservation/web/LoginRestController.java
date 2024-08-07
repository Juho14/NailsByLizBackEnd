package com.nailsbyliz.reservation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nailsbyliz.reservation.config.authtoken.CustomUserDetails;
import com.nailsbyliz.reservation.config.authtoken.JwtService;
import com.nailsbyliz.reservation.dto.AccountCredentialsDTO;
import com.nailsbyliz.reservation.repositories.AppUserRepository;

@CrossOrigin
@RestController
@PreAuthorize("permitAll()")
public class LoginRestController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    AppUserRepository userRepository;

    @PostMapping("/api/public/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getToken(@RequestBody AccountCredentialsDTO credentials) {
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(credentials.getUsername(),
                credentials.getPassword());
        Authentication auth = authManager.authenticate(creds);
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        // Generate authentication token
        String authToken = jwtService.generateAuthToken(userDetails);

        // Generate access token
        String accessToken = jwtService.generateAccessToken(userDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .header("Access-Token", "Bearer " + accessToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization, Access-Token")
                .build();
    }
}
