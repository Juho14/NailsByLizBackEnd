package com.nailsbyliz.reservation.config.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final String API_KEY_HEADER_NAME = "Api-Key";
    // private static final String EXPECTED_API_KEY = "test-api-key";
    private static final String API_KEY = System.getenv("CUSTOM_API_KEY");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // Check if the request is a POST request, rest need authentication elsewhere
        String apiKey = request.getHeader(API_KEY_HEADER_NAME);
        if (API_KEY.equals(apiKey)) {
            return true;
        }

        System.out.println("Api key error");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
        return false;

    }
}
