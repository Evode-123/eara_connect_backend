package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.Admin;
import com.earacg.earaConnect.model.User;
import com.earacg.earaConnect.repository.AdminRepository;
import com.earacg.earaConnect.repository.CommissionerGeneralRepository;
import com.earacg.earaConnect.repository.CommitteeMembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final AdminRepository adminRepository;
    private final CommissionerGeneralRepository commissionerGeneralRepository;
    private final CommitteeMembersRepository committeeMembersRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Generates a random password for new users
     */
    public String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
    
    /**
     * Updates a user's password and sets firstLogin to false
     */
    @Transactional
    public void updatePassword(User user, String newPassword) {
        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setFirstLogin(false);
            
            // Save the updated user based on type
            if (user.getRole().equals("ROLE_ADMIN")) {
                adminRepository.save((Admin) user);
            } else if (user.getRole().equals("COMMISSIONER_GENERAL")) {
                commissionerGeneralRepository.save((com.earacg.earaConnect.model.CommissionerGeneral) user);
            } else if (user.getRole().equals("COMMITTEE_MEMBER")) {
                committeeMembersRepository.save((com.earacg.earaConnect.model.CommitteeMembers) user);
            }
        } catch (DataAccessException e) {
            log.error("Database error while updating password: {}", e.getMessage());
            throw new RuntimeException("Failed to update password due to database error", e);
        }
    }
    
    /**
     * Changes a user's password after validating the old password
     */
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        try {
            // Find user
            User user = (User) loadUserByUsername(email);
            
            // Verify old password
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new BadCredentialsException("Invalid old password");
            }
            
            // Update password
            updatePassword(user, newPassword);
        } catch (DataAccessException e) {
            log.error("Database error during password change: {}", e.getMessage());
            throw new RuntimeException("Failed to change password due to database error", e);
        }
    }
    
    /**
     * Implements UserDetailsService to find users by email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Attempting to load user by email: {}", email);
            
            // Check in admin repository first
            Optional<Admin> adminOpt = adminRepository.findByEmail(email);
            if (adminOpt.isPresent()) {
                log.info("Found user in admin repository: {}", email);
                return adminOpt.get();
            }
            
            // Then check commissioner repository
            Optional<com.earacg.earaConnect.model.CommissionerGeneral> commissionerOpt = 
                    commissionerGeneralRepository.findByEmail(email);
            if (commissionerOpt.isPresent()) {
                log.info("Found user in commissioner repository: {}", email);
                return commissionerOpt.get();
            }
            
            // Finally check committee members repository - MODIFIED TO USE SPECIAL QUERY
            Optional<com.earacg.earaConnect.model.CommitteeMembers> committeeMemberOpt = 
                    committeeMembersRepository.findByEmailExcludingLob(email);
            if (committeeMemberOpt.isPresent()) {
                log.info("Found user in committee member repository: {}", email);
                return committeeMemberOpt.get();
            }
            
            log.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        } catch (DataAccessException e) {
            log.error("Database error while loading user {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to load user due to database error", e);
        }
    }
    
    /**
     * Find a user by email without throwing an exception if not found
     */
    public Optional<User> findUserByEmail(String email) {
        try {
            // Check in admin repository
            Optional<Admin> adminOpt = adminRepository.findByEmail(email);
            if (adminOpt.isPresent()) {
                return Optional.of(adminOpt.get());
            }
            
            // Check in commissioner repository
            Optional<com.earacg.earaConnect.model.CommissionerGeneral> commissionerOpt = 
                    commissionerGeneralRepository.findByEmail(email);
            if (commissionerOpt.isPresent()) {
                return Optional.of(commissionerOpt.get());
            }
            
            // Check in committee members repository
            Optional<com.earacg.earaConnect.model.CommitteeMembers> memberOpt = 
                    committeeMembersRepository.findByEmail(email);
            
            return memberOpt.map(member -> (User) member);
        } catch (DataAccessException e) {
            log.error("Database error while finding user by email {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to find user due to database error", e);
        }
    }

    @Transactional
    public void updateUserProfile(User user) {
        try {
            if (user.getRole().equals("ROLE_ADMIN")) {
                adminRepository.save((Admin) user);
            } else if (user.getRole().equals("COMMISSIONER_GENERAL")) {
                commissionerGeneralRepository.save((com.earacg.earaConnect.model.CommissionerGeneral) user);
            } else if (user.getRole().equals("COMMITTEE_MEMBER")) {
                committeeMembersRepository.save((com.earacg.earaConnect.model.CommitteeMembers) user);
            }
        } catch (DataAccessException e) {
            log.error("Database error while updating user profile: {}", e.getMessage());
            throw new RuntimeException("Failed to update user profile due to database error", e);
        }
    }
}