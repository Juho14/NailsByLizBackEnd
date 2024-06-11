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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor)
                .excludePathPatterns("/api/public/**")
                .addPathPatterns("/api/**");
    }
}
