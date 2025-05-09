package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.model.CommitteeMembers;
import com.earacg.earaConnect.model.MemberType;
import com.earacg.earaConnect.model.User;
import com.earacg.earaConnect.service.CommitteeMembersService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/committee-members")
public class CommitteeMembersController {
    private final CommitteeMembersService committeeMembersService;
    
    public CommitteeMembersController(CommitteeMembersService committeeMembersService) {
        this.committeeMembersService = committeeMembersService;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllCommitteeMembers() {
        return ResponseEntity.ok(committeeMembersService.getAllCommitteeMembers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(committeeMembersService.getMemberById(id));
    }
    
    @PostMapping(value = "/country/{countryId}/position/{positionId}/authority/{revenueAuthorityId}",
             consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMember(
        @PathVariable Long countryId,
        @PathVariable Long positionId,
        @PathVariable Long revenueAuthorityId,
        @RequestParam(required = false) MemberType memberType,
        @RequestPart("member") String memberStr,
        @RequestPart("appointedLetter") MultipartFile appointedLetter) throws IOException {
        
        ObjectMapper objectMapper = new ObjectMapper();
        CommitteeMembers member = objectMapper.readValue(memberStr, CommitteeMembers.class);
        
        return ResponseEntity.ok(
            committeeMembersService.createMember(member, countryId, positionId, 
                                              revenueAuthorityId, memberType, appointedLetter)
        );
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMember(
            @PathVariable Long id,
            @RequestParam(required = false) MemberType memberType,
            @RequestPart("member") String memberStr,
            @RequestPart(required = false) MultipartFile appointedLetter) throws IOException {
        
        ObjectMapper objectMapper = new ObjectMapper();
        CommitteeMembers memberDetails = objectMapper.readValue(memberStr, CommitteeMembers.class);
        
        return ResponseEntity.ok(
            committeeMembersService.updateMember(id, memberDetails, memberType, appointedLetter)
        );
    }
    
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateMemberProfile(
            @PathVariable Long id,
            @RequestParam User.Gender gender,
            @RequestParam String jobPosition,
            @RequestParam String department,
            @RequestParam Date employmentDate) {
        return ResponseEntity.ok(
            committeeMembersService.updateMemberProfile(id, gender, jobPosition, department, employmentDate)
        );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        committeeMembersService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/letter")
    public ResponseEntity<byte[]> downloadAppointedLetter(@PathVariable Long id) {
        byte[] letter = committeeMembersService.getAppointedLetter(id);
        String fileName = committeeMembersService.getAppointedLetterName(id);
        String contentType = committeeMembersService.getAppointedLetterType(id);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileName + "\"")
            .body(letter);
    }
}