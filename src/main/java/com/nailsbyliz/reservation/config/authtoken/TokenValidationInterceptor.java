package com.nailsbyliz.reservation.config.authtoken;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // Check if the method is annotated with @PreAuthorize("permitAll()")
        if (method.isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
            if (preAuthorize.value().equals("permitAll()")) {
                return true;
            }
        }

        // Extract and validate access token
        String accessToken = jwtService.resolveAccessToken(request);
        if (accessToken == null || !jwtService.validateToken(accessToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token not provided or invalid");
            return false;
        }

        // Extract and validate auth token
        String authToken = jwtService.resolveAuthToken(request);
        if (authToken == null || !jwtService.validateToken(authToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Auth token not provided or invalid");
            return false;
        }

        // Get authenticated user details
        UsernamePasswordAuthenticationToken authentication = jwtService.getAuthUser(request);
        if (authentication == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User authentication failed");
            return false;
        }

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Set request attributes based on the auth token
        setRequestAttributes(request, authToken, jwtService);

        // Check if the role is "ROLE_ADMIN"
        String role = (String) request.getAttribute("role");
        if ("ROLE_ADMIN".equals(role)) {
            return true;
        }

        // Check for hasAnyRole annotation
        if (method.isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
            if (preAuthorize.value().startsWith("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")) {
                if (role != null) {
                    return true;
                }
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Role not provided or invalid");
                return false;
            }
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
        return false;
    }

    private void setRequestAttributes(HttpServletRequest request, String token, JwtService jwtService) {
        Long userId = jwtService.getIdFromToken(token);
        request.setAttribute("id", userId);
        String role = jwtService.getRoleFromToken(token);
        request.setAttribute("role", role);
        System.out.println("UserId: " + userId + ", Role: " + role);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // Do something after the request has been handled by the controller
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        // Do something after the request has been completed
    }
}