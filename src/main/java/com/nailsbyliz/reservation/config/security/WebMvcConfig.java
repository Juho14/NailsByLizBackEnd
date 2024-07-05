package com.nailsbyliz.reservation.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nailsbyliz.reservation.config.authtoken.TokenValidationInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TokenValidationInterceptor tokenValidationInterceptor;

    @Autowired
    private ApiKeyInterceptor apiKeyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor)
                .excludePathPatterns("/api/public/**")
                .addPathPatterns("/api/**");

        registry.addInterceptor(apiKeyInterceptor)
                .addPathPatterns("/api/reservations")
                .addPathPatterns("/api/reservationsettings/active")
                .addPathPatterns("/api/nailservices")
                .addPathPatterns("/api/nailservices/**")
                .addPathPatterns("/api/reservations/byday/**")
                .addPathPatterns("/api/reservations/byweek/**")
                .addPathPatterns("/api/public/token")
                .addPathPatterns("/api/users/register")
                .addPathPatterns("/api/public/login");
        // .addPathPatterns("/api/public/token");

    }
}
