package com.nailsbyliz.reservation.config.authtoken;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // Check if the method has the @PreAuthorize("permitAll()") annotation
        if (method.isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
            if (preAuthorize.value().equals("permitAll()")) {
                // Allow access to endpoints with permitAll() without authentication
                return true;
            }
        }

        String token = jwtService.resolveToken(request);
        if (token != null) {
            if (jwtService.validateToken(token)) {
                // Token is valid
                Long userId = jwtService.getIdFromToken(token);
                request.setAttribute("userId", userId);
                String role = jwtService.getRoleFromToken(token);
                request.setAttribute("userRole", role);
                return true;
            } else {
                // Token is invalid
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return false;
            }
        } else {
            // Token is not present
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token not provided");
            return false;
        }
    }

    // Implementing other methods of HandlerInterceptor if needed
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
