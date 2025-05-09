package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.dto.AdminRegistrationRequest;
import com.earacg.earaConnect.model.Admin;
import com.earacg.earaConnect.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for admin management operations
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    
    /**
     * Create a new admin (only accessible to existing admins)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdmin(@RequestBody AdminRegistrationRequest request) {
        // Check if email already exists
        if (adminService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use"));
        }
        
        // Create new admin
        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        
        adminService.createAdmin(admin);
        
        return ResponseEntity.ok().body(Map.of("message", "Admin created successfully"));
    }
    
    /**
     * Get all admins (only accessible to existing admins)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }
    
    /**
     * Get admin by ID (only accessible to existing admins)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAdminById(id));
    }
}