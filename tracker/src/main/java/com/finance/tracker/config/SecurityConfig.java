package com.finance.tracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.finance.tracker.security.JwtAuthFilter;
import com.finance.tracker.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired 
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired 
    private JwtAuthFilter jwtAuthFilter;

    // ── Password Encoder ─────────────────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Authentication Provider ──────────────────────────────────────────────
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── Authentication Manager ────────────────────────────────────────────────
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── HTTP Security ────────────────────────────────────────────────────────
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disabled for JWT usage
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No server-side sessions
            .authorizeHttpRequests(auth -> auth
                // Public endpoints: Auth APIs and Static Frontend Files
                .requestMatchers(
                    "/api/auth/**",       // Registration & Login APIs
                    "/",                  // Root
                    "/index.html",        // Signup Page
                    "/login.html",        // Login Page (Added to fix 403)
                    "/dashboard.html",    // Dashboard (JS will handle token check)
                    "/static/**",         // Static folder resources
                    "/css/**",            // CSS styles
                    "/js/**",             // JavaScript files
                    "/favicon.ico",       // Browser icon
                    "/h2-console/**"      // H2 Database console (Dev only)
                ).permitAll()
                
                // Admin-only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                //Everything else requires authentication (valid JWT)
                .anyRequest().authenticated()
                
            )
            // Allow H2 console to render in iframes (dev only)
            .headers(h -> h.frameOptions(f -> f.disable()))
            // Plug in our JWT filter before Spring's default username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authProvider());

        return http.build();
    }
}