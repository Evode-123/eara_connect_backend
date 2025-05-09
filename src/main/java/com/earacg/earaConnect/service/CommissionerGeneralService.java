package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.*;
import com.earacg.earaConnect.repository.CommissionerGeneralHistoryRepository;
import com.earacg.earaConnect.repository.CommissionerGeneralRepository;
import com.earacg.earaConnect.repository.CountryRepository;
import com.earacg.earaConnect.repository.RevenueAuthorityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionerGeneralService {
    private final CommissionerGeneralRepository commissionerGeneralRepository;
    private final CommissionerGeneralHistoryRepository historyRepository;
    private final CountryRepository countryRepository;
    private final RevenueAuthorityRepository revenueAuthorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserService userService;

    public List<CommissionerGeneral> getAllCommissioners() {
        return commissionerGeneralRepository.findAll();
    }

    public CommissionerGeneral getCommissionerById(Long id) {
        return commissionerGeneralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commissioner General not found with id: " + id));
    }

    @Transactional
    public CommissionerGeneral createCommissioner(CommissionerGeneral commissioner, Long countryId, 
                                                Long revenueAuthorityId, MemberType memberType) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
        
        RevenueAuthority revenueAuthority = revenueAuthorityRepository.findById(revenueAuthorityId)
                .orElseThrow(() -> new RuntimeException("Revenue Authority not found with id: " + revenueAuthorityId));
        
        // Debug logging
        System.out.println("Creating commissioner: " + commissioner);
        System.out.println("cgName: " + commissioner.getCgName());
        System.out.println("cgEmail: " + commissioner.getCgEmail());
        System.out.println("cgPhone: " + commissioner.getCgPhone());
        System.out.println("memberType: " + memberType);
        
        // Ensure commissioner has proper values set
        commissioner.setCountry(country);
        commissioner.setRevenueAuthority(revenueAuthority);
        
        // Set memberType if provided, otherwise default to MEMBER
        commissioner.setMemberType(memberType != null ? memberType : MemberType.MEMBER);
        
        // Generate a random password
        String rawPassword = userService.generateRandomPassword();
        commissioner.setPassword(passwordEncoder.encode(rawPassword));
        
        // Set email address (make sure it's assigned properly)
        if (commissioner.getEmail() == null && commissioner.getCgEmail() != null) {
            commissioner.setEmail(commissioner.getCgEmail());
        }
        
        // Set first login flag
        commissioner.setFirstLogin(true);
        
        // Set the role
        commissioner.setRole("COMMISSIONER_GENERAL");
        
        // Save the commissioner
        CommissionerGeneral savedCommissioner = commissionerGeneralRepository.save(commissioner);
        
        // Log the credentials that will be sent
        System.out.println("Commissioner General credentials created:");
        System.out.println("Email: " + commissioner.getCgEmail());
        System.out.println("Password: " + rawPassword);
        
        // Send credentials via email
        emailService.sendCredentials(commissioner.getCgEmail(), rawPassword);
        
        return savedCommissioner;
    }

    @Transactional
    public CommissionerGeneral updateCommissioner(Long id, CommissionerGeneral commissionerDetails, 
                                               MemberType memberType) {
        CommissionerGeneral existing = getCommissionerById(id);
        
        // Save to history before updating
        saveToHistory(existing);
        
        existing.setCgName(commissionerDetails.getCgName());
        existing.setCgPhone(commissionerDetails.getCgPhone());
        existing.setCgEmail(commissionerDetails.getCgEmail());
        
        // Update member type if changed
        if (memberType != null) {
            existing.setMemberType(memberType);
        }
        
        return commissionerGeneralRepository.save(existing);
    }

    @Transactional
    public CommissionerGeneral updateCommissionerProfile(Long id, User.Gender gender, String jobPosition, 
                                                      String department, java.util.Date employmentDate) {
        CommissionerGeneral existing = getCommissionerById(id);
        
        // Save to history before updating
        saveToHistory(existing);
        
        existing.setGender(gender);
        existing.setCurrentJobPosition(jobPosition);
        existing.setDepartment(department);
        existing.setEmploymentDate(employmentDate);
        
        return commissionerGeneralRepository.save(existing);
    }

    private void saveToHistory(CommissionerGeneral commissioner) {
        CommissionerGeneralHistory history = new CommissionerGeneralHistory();
        history.setCgName(commissioner.getCgName());
        history.setCgPhone(commissioner.getCgPhone());
        history.setCgEmail(commissioner.getCgEmail());
        history.setCountry(commissioner.getCountry());
        history.setRevenueAuthority(commissioner.getRevenueAuthority());
        history.setMemberType(commissioner.getMemberType());
        history.setGender(commissioner.getGender());
        history.setCurrentJobPosition(commissioner.getCurrentJobPosition());
        history.setDepartment(commissioner.getDepartment());
        history.setEmploymentDate(commissioner.getEmploymentDate());
        history.setUpdatedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Transactional
    public void deleteCommissioner(Long id) {
        commissionerGeneralRepository.deleteById(id);
    }
}