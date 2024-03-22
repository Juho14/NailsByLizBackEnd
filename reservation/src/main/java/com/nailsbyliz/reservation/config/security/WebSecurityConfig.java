package com.nailsbyliz.reservation.config.security;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nailsbyliz.reservation.web.UserDetailServiceImpl;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
    private final UserDetailServiceImpl userDetailsService;

    public WebSecurityConfig(UserDetailServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(antMatcher("/css/**")).permitAll()
                        .requestMatchers(antMatcher("/login")).permitAll()
                        .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(antMatcher("/api/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/reservations")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/reservations")).permitAll()
                        .requestMatchers(antMatcher("/api/reservationsettings/active")).permitAll()
                        /*
                         * .requestMatchers(antMatcher(HttpMethod.GET,
                         * "/api/reservations/**")).permitAll()
                         * .requestMatchers(antMatcher(HttpMethod.POST,
                         * "/api/reservations/**")).permitAll()
                         * .requestMatchers(antMatcher(HttpMethod.PUT,
                         * "/api/reservations/**")).permitAll()
                         * .requestMatchers(antMatcher(HttpMethod.DELETE,
                         * "/api/reservations/**")).permitAll()
                         */
                        .anyRequest().authenticated())
                .headers(headers -> headers
                        .frameOptions(frameoptions -> frameoptions.disable()) // for h2 console
                )
                .formLogin(formlogin -> formlogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll())

                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
