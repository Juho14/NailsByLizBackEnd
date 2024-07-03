package com.nailsbyliz.reservation.config.security;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nailsbyliz.reservation.service.UserDetailServiceImpl;

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
                        /*
                         * .requestMatchers(antMatcher("/login")).permitAll()
                         * .requestMatchers(antMatcher(HttpMethod.GET, "/api/reservations")).permitAll()
                         * .requestMatchers(antMatcher(HttpMethod.POST,
                         * "/api/reservations")).permitAll()
                         * .requestMatchers(antMatcher("/api/reservationsettings/active")).permitAll()
                         * .requestMatchers(antMatcher("/api/login")).permitAll()
                         */
                        // .requestMatchers(antMatcher("/api/public/**")).permitAll()
                        // .anyRequest().hasRole("ADMIN"))

                        .requestMatchers(antMatcher("/**")).permitAll()
                        .anyRequest().authenticated())

                .headers(headers -> headers.frameOptions(frameoptions -> frameoptions.disable()))
                .formLogin(formlogin -> formlogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/api/reservations", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
