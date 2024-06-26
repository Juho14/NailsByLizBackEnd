package com.nailsbyliz.reservation.config.authtoken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthEntry exceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {
        String jws = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (jws != null) {
            try {
                SecurityContextHolder.getContext().setAuthentication(jwtService.getAuthUser(request));
            } catch (ExpiredJwtException e) {
                // Handle expired token without logging
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Token has expired");
                return;
            } catch (AuthenticationException authException) {
                exceptionHandler.commence(request, response, authException);
            }
        }
        filterChain.doFilter(request, response);
    }
}