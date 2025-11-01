package com.filipecode.papertrading.infrastructure.security.config;

import com.filipecode.papertrading.infrastructure.security.SecurityFilter;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP, 
    bearerFormat = "JWT", 
    scheme = "bearer")
@Profile("!dev")
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints Públicos
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "swagger-ui/**", "swagger-ui.html").permitAll()

                        // Endpoints que exigem a ROLE_USER
                        .requestMatchers("/api/v1/portfolios/**").hasRole("USER")
                        .requestMatchers("/api/v1/orders/**").hasRole("USER")
                        .requestMatchers("/api/v1/transactions/**").hasRole("USER")
                        .requestMatchers("/api/v1/users/**").hasRole("USER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}