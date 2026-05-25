package com.tnc.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// From Springboot-starter-security -> helps in password encoding !
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
public class SecurityConfig {

    // The spring-boot-starter-security bydefault proctects all endpoints.
    // Below method is to disable CSRF and allow "/auth/login" & "/auth/register" endpoint for testing !!
    // sessionManagement is added as JWT APIs are stateless.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/login", "/auth/register").permitAll()
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // using BCryptPasswordEncoder from spring-security as a bean to be available to dependency injection.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
