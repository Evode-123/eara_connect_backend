package com.earacg.earaConnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private Long id;
    private boolean firstLogin;
    private boolean profileComplete;
    private String role;
    private String memberType; // Added for committee member type
}