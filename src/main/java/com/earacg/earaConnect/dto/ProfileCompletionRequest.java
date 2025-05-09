package com.earacg.earaConnect.dto;

import com.earacg.earaConnect.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class ProfileCompletionRequest {
    private User.Gender gender;
    private String jobPosition;
    private String department;
    private Date employmentDate;
}