package com.earacg.earaConnect.service;

import com.earacg.earaConnect.dto.AuthRequest;
import com.earacg.earaConnect.dto.AuthResponse;
import com.earacg.earaConnect.dto.PasswordChangeRequest;
import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.model.CommitteeMembers;
import com.earacg.earaConnect.model.MemberType;
import com.earacg.earaConnect.model.User;
import com.earacg.earaConnect.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(AuthRequest request) {
        try {
            log.info("Authenticating user: {}", request.getEmail());
            
            // Authenticate through Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Get user details
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            final User user = (User) userDetails;
            
            // Generate JWT token
            final String jwt = jwtUtil.generateToken(userDetails);
            
            // Get user status
            final boolean firstLogin = user.isFirstLogin();
            final boolean profileComplete = isProfileComplete(user);
            final String role = user.getRole();
            final String email = user.getEmail();
            final Long id = user.getId();

            // Check member type - ensure null safety
            MemberType memberType = null;
            
            // Add specific checks for each user type
            if (user instanceof CommissionerGeneral) {
                memberType = ((CommissionerGeneral) user).getMemberType();
            } else if (user instanceof CommitteeMembers) {
                // Add other specific data for committee members if needed
            }

            log.info("Authentication successful for user: {}", email);
            
            // Build response with ALL required fields
            return AuthResponse.builder()
                .token(jwt)
                .email(email)
                .id(id)
                .firstLogin(firstLogin)
                .profileComplete(profileComplete)
                .role(role)
                .memberType(memberType != null ? memberType.name() : null)
                .build();
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new RuntimeException("Authentication failed", e);
        }
    }

    private boolean isProfileComplete(User user) {
        // Check if all required profile fields are filled
        return user.getGender() != null &&
               user.getCurrentJobPosition() != null && 
               !user.getCurrentJobPosition().isEmpty() &&
               user.getDepartment() != null && 
               !user.getDepartment().isEmpty() &&
               user.getEmploymentDate() != null;
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request, String email) {
        try {
            log.info("Changing password for user: {}", email);
            
            // Validate request
            if (request == null) {
                log.error("Password change request is null for user: {}", email);
                throw new RuntimeException("Password change request cannot be null");
            }
            
            if (request.getOldPassword() == null || request.getNewPassword() == null) {
                log.error("Missing password fields in request for user: {}", email);
                throw new RuntimeException("Old password and new password must be provided");
            }
            
            // Load user
            User user = (User) userService.loadUserByUsername(email);
            log.info("User loaded for password change: {}", email);
            
            // Debug the password validation (without exposing the actual passwords)
            boolean passwordMatches = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
            log.info("Password validation result for user {}: {}", email, passwordMatches ? "matches" : "does not match");
            
            // Validate old password
            if (!passwordMatches) {
                log.warn("Old password validation failed for user: {}", email);
                throw new RuntimeException("Old password is incorrect");
            }
            
            // Set new password and update firstLogin flag
            userService.updatePassword(user, request.getNewPassword());
            log.info("Password changed successfully for user: {}", email);
        } catch (DataAccessException e) {
            log.error("Database error during password change for email: {}", email);
            log.error("Error message: {}", e.getMessage());
            throw new RuntimeException("Database error during password change", e);
        }
    }
}