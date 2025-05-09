package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.dto.AuthRequest;
import com.earacg.earaConnect.dto.AuthResponse;
import com.earacg.earaConnect.dto.PasswordChangeRequest;
import com.earacg.earaConnect.dto.ProfileCompletionRequest;
import com.earacg.earaConnect.model.User;
import com.earacg.earaConnect.service.AuthService;
import com.earacg.earaConnect.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            log.info("Login request received for: {}", authRequest.getEmail());
            AuthResponse response = authService.authenticate(authRequest);
            log.info("Login successful for: {}", authRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Authentication failed for {}: {}", 
                    authRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: " + e.getMessage());
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        try {
            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                log.error("No authentication found in SecurityContext");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            String email = authentication.getName();
            log.info("Password change request received for user: {}", email);
            
            // Debug request content (without showing actual passwords)
            log.info("Password change request fields: oldPassword={}, newPassword={}", 
                passwordChangeRequest.getOldPassword() != null ? "[PROVIDED]" : "[MISSING]",
                passwordChangeRequest.getNewPassword() != null ? "[PROVIDED]" : "[MISSING]");
            
            // Call service method to change password
            authService.changePassword(passwordChangeRequest, email);
            
            log.info("Password changed successfully for user: {}", email);
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (Exception e) {
            log.error("Password change failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Password change failed: " + e.getMessage());
        }
    }
    
    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestBody ProfileCompletionRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("Profile completion request received for user: {}", email);
            
            User user = (User) userService.loadUserByUsername(email);
            
            // Update profile fields
            user.setGender(request.getGender());
            user.setCurrentJobPosition(request.getJobPosition());
            user.setDepartment(request.getDepartment());
            user.setEmploymentDate(request.getEmploymentDate());
            
            // Save the updated user
            userService.updateUserProfile(user);
            
            log.info("Profile completed successfully for user: {}", email);
            return ResponseEntity.ok().body("Profile completed successfully");
        } catch (Exception e) {
            log.error("Profile completion failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Profile completion failed: " + e.getMessage());
        }
    }
}