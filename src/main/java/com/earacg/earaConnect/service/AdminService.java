package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.Admin;
import com.earacg.earaConnect.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    @Value("${admin.default.email:admin@earaconnect.org}")
    private String defaultAdminEmail;
    
    /**
     * Create a new admin with a random password
     */
    @Transactional
    public Admin createAdmin(Admin admin) {
        // Generate random password
        String rawPassword = userService.generateRandomPassword();
        
        // Encode password and set first login flag
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setFirstLogin(true);
        
        // Set the role
        admin.setRole("ADMIN");
        
        // Save admin
        Admin savedAdmin = adminRepository.save(admin);
        
        // Send credentials email
        sendAdminCredentialsEmail(savedAdmin.getEmail(), rawPassword);
        
        return savedAdmin;
    }
    
    /**
     * Send admin credentials email
     */
    public void sendAdminCredentialsEmail(String email, String password) {
        String subject = "Your EARA Connect Admin Account";
        String body = String.format(
            "Dear %s,\n\n" +
            "Your admin account has been created in the EARA Connect system. Here are your login credentials:\n\n" +
            "Username: %s\n" +
            "Password: %s\n\n" +
            "You will be required to change your password upon first login.\n\n" +
            "Regards,\n" +
            "EARA Administration",
            email, 
            email,
            password
        );
        
        emailService.sendEmail(email, subject, body);
    }
    
    /**
     * Initialize the default admin account if no admins exist
     */
    @Transactional
    public void initializeDefaultAdmin() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setEmail(defaultAdminEmail);
            
            // Generate a random password for the default admin
            String password = userService.generateRandomPassword();
            admin.setPassword(passwordEncoder.encode(password));
            admin.setFirstLogin(true);
            
            // Set the role for the default admin
            admin.setRole("ADMIN");
            
            adminRepository.save(admin);
            
            // Send credentials to the default admin
            sendAdminCredentialsEmail(admin.getEmail(), password);
            
            System.out.println("Default admin account created: " + defaultAdminEmail);
            System.out.println("Default Password"+password);
        }
    }
    
    /**
     * Get all admins
     */
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
    
    /**
     * Get admin by ID
     */
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
    }
    
    /**
     * Find admin by email
     */
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
    
    /**
     * Check if an admin with the given email exists
     */
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }
}