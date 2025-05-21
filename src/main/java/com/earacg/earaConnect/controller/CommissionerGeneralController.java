package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.model.MemberType;
import com.earacg.earaConnect.model.User;
import com.earacg.earaConnect.service.CommissionerGeneralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/commissioners")
public class CommissionerGeneralController {
    private final CommissionerGeneralService commissionerGeneralService;
    
    public CommissionerGeneralController(CommissionerGeneralService commissionerGeneralService) {
        this.commissionerGeneralService = commissionerGeneralService;
    }
    
    @GetMapping("/get-all")
    public ResponseEntity<List<CommissionerGeneral>> getAllCommissioners() {
        return ResponseEntity.ok(commissionerGeneralService.getAllCommissioners());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommissionerGeneral> getCommissionerById(@PathVariable Long id) {
        return ResponseEntity.ok(commissionerGeneralService.getCommissionerById(id));
    }
    
    @PostMapping("/country/{countryId}/authority/{revenueAuthorityId}")
    public ResponseEntity<CommissionerGeneral> createCommissioner(
            @PathVariable Long countryId,
            @PathVariable Long revenueAuthorityId,
            @RequestParam(required = false, defaultValue = "MEMBER") MemberType memberType,
            @RequestBody CommissionerGeneral commissioner) {
        
        // Debug logging to see what's being received
        System.out.println("Received commissioner data: " + commissioner);
        System.out.println("cgName: " + commissioner.getCgName());
        System.out.println("cgEmail: " + commissioner.getCgEmail());
        System.out.println("cgPhone: " + commissioner.getCgPhone());
        System.out.println("memberType: " + memberType);
        
        return ResponseEntity.ok(commissionerGeneralService.createCommissioner(
                commissioner, countryId, revenueAuthorityId, memberType));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommissionerGeneral> updateCommissioner(
            @PathVariable Long id,
            @RequestParam(required = false) MemberType memberType,
            @RequestBody CommissionerGeneral commissionerDetails) {
        return ResponseEntity.ok(commissionerGeneralService.updateCommissioner(
                id, commissionerDetails, memberType));
    }
    
    @PutMapping("/{id}/profile")
    public ResponseEntity<CommissionerGeneral> updateCommissionerProfile(
            @PathVariable Long id,
            @RequestParam User.Gender gender,
            @RequestParam String jobPosition,
            @RequestParam String department,
            @RequestParam Date employmentDate) {
        return ResponseEntity.ok(commissionerGeneralService.updateCommissionerProfile(
                id, gender, jobPosition, department, employmentDate));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommissioner(@PathVariable Long id) {
        commissionerGeneralService.deleteCommissioner(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<CommissionerGeneral>> getCommissionersByCountry(@PathVariable Long countryId) {
        return ResponseEntity.ok(commissionerGeneralService.getCommissionersByCountryId(countryId));
    }
}