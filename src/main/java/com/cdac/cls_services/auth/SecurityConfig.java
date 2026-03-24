package com.cdac.cls_services.auth;

import jakarta.security.auth.message.config.AuthConfigProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig  {

    private JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    // ── 1. The main security filter chain ─────────────────────────────────────
    // This bean defines the rules for every incoming HTTP request

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                // Disable CSRF — safe to do when using stateless JWT
                // CSRF protects against form-based attacks on session cookies
                // Since we validate JWT on every request, CSRF is not needed
                .csrf(AbstractHttpConfigurer::disable)

                // Apply our CORS configuration (defined below)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Define which routes are public and which require authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()     // Allow login and logout without a token
                        .anyRequest().authenticated()        // Every other request must have a valid JWT
                )

                // Tell Spring Security to NOT create HTTP sessions
                // Each request must carry its own JWT — no server-side session memory
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Register our authentication provider (DB + BCrypt)
                .authenticationProvider(authenticationProvider())

                // Insert our JwtAuthFilter BEFORE Spring's default login filter
                // This ensures JWT is checked before anything else
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    // ── 2. CORS Configuration ─────────────────────────────────────────────────
    // CORS = Cross-Origin Resource Sharing
    // Your Angular app runs on localhost:4200, Spring on localhost:8080
    // Browsers block cross-origin requests by default — this allows them

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from your Angular dev server
        // In production, replace with your actual deployed frontend URL
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allow these HTTP methods
        config.setAllowedMethods(List.of("GET", "POST"));

        // Allow all headers (Content-Type, Authorization etc.)
        config.setAllowedHeaders(List.of("*"));

        // CRITICAL: Allow cookies to be sent cross-origin
        // Without this, the HttpOnly JWT cookie won't be sent by the browser
        config.setAllowCredentials(true);

        // Apply this CORS config to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ── 3. Authentication Provider ────────────────────────────────────────────
    // This tells Spring HOW to authenticate a user:
    // — Where to load the user from (our UserDetailsServiceImpl → user_m table)
    // — How to verify the password (BCrypt)

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── 4. Password Encoder ───────────────────────────────────────────────────
    // BCrypt is a one-way hashing algorithm for passwords
    // When user logs in: BCrypt hashes the entered password and
    // compares it to the stored hash — the plain text is never stored
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── 5. Authentication Manager ─────────────────────────────────────────────
    // The AuthenticationManager is what actually performs the login check
    // Our AuthController will use this to trigger authentication

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
