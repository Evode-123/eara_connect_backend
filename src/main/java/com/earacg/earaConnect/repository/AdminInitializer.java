package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes the default admin account when the application starts
 */
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AdminService adminService;

    @Override
    public void run(String... args) {
        // Initialize the default admin account if no admins exist
        adminService.initializeDefaultAdmin();
    }
}