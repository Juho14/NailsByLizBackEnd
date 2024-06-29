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
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        if (method.isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
            if (preAuthorize.value().equals("permitAll()")) {
                return true;
            }
        }

        String accessToken = jwtService.resolveAccessToken(request);
        if (accessToken != null && jwtService.validateToken(accessToken)) {
            setRequestAttributes(request, accessToken, jwtService);
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token not provided or invalid");
        return false;
    }

    private void setRequestAttributes(HttpServletRequest request, String token, JwtService jwtService) {
        Long userId = jwtService.getIdFromAccessToken(token);
        request.setAttribute("userId", userId);
        String role = jwtService.getRoleFromAccessToken(token);
        request.setAttribute("role", role);
        System.out.println(role);
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