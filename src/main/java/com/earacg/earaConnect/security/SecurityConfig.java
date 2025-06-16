package com.earacg.earaConnect.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.earacg.earaConnect.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final UserService userDetailsService;
    private final JwtFilter jwtFilter;
    
    public SecurityConfig(@Lazy UserService userDetailsService, @Lazy JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().configurationSource(corsConfigurationSource()).and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                
                // Authentication required endpoints
                .requestMatchers("/api/auth/change-password").hasAnyAuthority("ROLE_ADMIN", "ROLE_COMMISSIONER_GENERAL", "ROLE_COMMITTEE_MEMBER")
                .requestMatchers("/api/auth/complete-profile").authenticated()
                
                // Admin only endpoints - Admin should have access to everything
                .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                
                // Meeting minutes endpoints - Updated with proper role authorities
                .requestMatchers("/api/meeting-minutes/create-basic").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN", 
                    "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                )
                
                .requestMatchers("/api/meeting-minutes/**").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN", 
                    "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                )

                .requestMatchers("/api/meeting-minutes/create-basic").hasAnyAuthority(
                        "ROLE_SECRETARY", "SECRETARY", 
                        "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                        "ROLE_ADMIN", "ADMIN",  // Both formats
                        "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                    )

                .requestMatchers("/api/meeting-minutes/documents/download/**").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN",
                    "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                )

                .requestMatchers("/api/meeting-minutes/upcoming/**").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN", 
                    "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                )
                
                // Secretary related endpoints
                .requestMatchers("/api/secretary/**").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN"
                )
                
                // Entity access endpoints
                .requestMatchers("/api/commissioner-generals/**").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN", 
                    "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                )
                .requestMatchers("/api/committee-members/**").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN", 
                    "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                )
                .requestMatchers("/api/positions/**").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN", 
                    "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                )
                .requestMatchers("/api/countries/**").hasAnyAuthority(
                    "ROLE_SECRETARY", "SECRETARY", 
                    "ROLE_COMMISSIONER_GENERAL", "COMMISSIONER_GENERAL", 
                    "ROLE_ADMIN", "ADMIN", 
                    "ROLE_COMMITTEE_MEMBER", "COMMITTEE_MEMBER"
                )
                
                // Any other request must be authenticated
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}