package com.earacg.earaConnect.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.model.CommitteeMembers;
import com.earacg.earaConnect.model.MemberType;
import com.earacg.earaConnect.model.User;
import com.earacg.earaConnect.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserService userService;
    
    public JwtFilter(JwtUtil jwtUtil, @Lazy UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        
        log.debug("Processing request for URI: {}", requestURI);
        
        String jwt = null;
        String userEmail = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                userEmail = jwtUtil.extractUsername(jwt);
                log.debug("Extracted username from JWT: {}", userEmail);
            } catch (Exception e) {
                log.error("Error extracting username from JWT: {}", e.getMessage());
            }
        } else {
            log.debug("No Authorization header or doesn't start with Bearer");
        }
        
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userService.loadUserByUsername(userEmail);
                log.debug("User loaded successfully: {}", userEmail);
                
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    User user = (User) userDetails;
                    
                    // Create a collection to hold authorities (roles)
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    
                    // Add the user's role
                    String roleName = "ROLE_" + user.getRole();
                    authorities.add(new SimpleGrantedAuthority(roleName));
                    log.debug("Added role: {}", roleName);
                    
                    // Check if the user is a secretary (either CommissionerGeneral or CommitteeMembers)
                    if (user instanceof CommissionerGeneral) {
                        CommissionerGeneral cg = (CommissionerGeneral) user;
                        if (cg.getMemberType() == MemberType.SECRETARY) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_SECRETARY"));
                            log.debug("Added ROLE_SECRETARY for CommissionerGeneral");
                        }
                    } else if (user instanceof CommitteeMembers) {
                        CommitteeMembers cm = (CommitteeMembers) user;
                        if (cm.getMemberType() == MemberType.SECRETARY) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_SECRETARY"));
                            log.debug("Added ROLE_SECRETARY for CommitteeMembers");
                        }
                    }
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Authenticated user: {} with authorities: {}", userEmail, authorities);
                } else {
                    log.debug("Token validation failed for user: {}", userEmail);
                }
            } catch (Exception e) {
                log.error("Error during authentication: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}