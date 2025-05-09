package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.dto.MeetingMinuteRequest;
import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.model.CommitteeMembers;
import com.earacg.earaConnect.model.MeetingMinute;
import com.earacg.earaConnect.service.MeetingMinuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/meeting-minutes")
@RequiredArgsConstructor
public class MeetingMinuteController {

    private final MeetingMinuteService meetingMinuteService;

    @GetMapping
    public ResponseEntity<List<MeetingMinute>> getAllMeetingMinutes() {
        return ResponseEntity.ok(meetingMinuteService.getAllMeetingMinutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingMinute> getMeetingMinuteById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingMinuteService.getMeetingMinuteById(id));
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<MeetingMinute> createMeetingMinute(
            @PathVariable Long userId,
            @RequestBody MeetingMinuteRequest request) {
        
        // Get authenticated user's role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = "";
        
        // Extract the role, handling potential formats (ROLE_XXX or just XXX)
        if (authentication != null && !authentication.getAuthorities().isEmpty()) {
            String authority = authentication.getAuthorities().iterator().next().getAuthority();
            userRole = authority;
            // Log the extracted role for debugging
            System.out.println("Extracted user role: " + userRole);
        }
        
        // If no role was extracted from authentication, try to use the one from request
        if (userRole.isEmpty() && request.getUserRole() != null && !request.getUserRole().isEmpty()) {
            userRole = request.getUserRole();
            System.out.println("Using role from request: " + userRole);
        }
        
        MeetingMinute meetingMinute = new MeetingMinute();
        meetingMinute.setMeetingNo(request.getMeetingNo());
        meetingMinute.setDate(request.getDate());
        meetingMinute.setLocation(request.getLocation());
        meetingMinute.setMeetingType(request.getMeetingType());
        meetingMinute.setTheme(request.getTheme());
        
        MeetingMinute savedMeetingMinute = meetingMinuteService.createMeetingMinute(
            meetingMinute,
            userId,
            request.getCommitteeParticipantIds(),
            request.getCommissionerParticipantIds(),
            request.getAgendaItems(),
            request.getResolutions(),
            userRole
        );
        
        return ResponseEntity.ok(savedMeetingMinute);
    }

    // Other methods remain unchanged
    @PutMapping("/{id}")
    public ResponseEntity<MeetingMinute> updateMeetingMinute(
            @PathVariable Long id,
            @RequestBody MeetingMinuteRequest request) {
        
        MeetingMinute meetingMinute = new MeetingMinute();
        meetingMinute.setMeetingNo(request.getMeetingNo());
        meetingMinute.setDate(request.getDate());
        meetingMinute.setLocation(request.getLocation());
        meetingMinute.setMeetingType(request.getMeetingType());
        meetingMinute.setTheme(request.getTheme());
        
        MeetingMinute updatedMeetingMinute = meetingMinuteService.updateMeetingMinute(
            id,
            meetingMinute,
            request.getCommitteeParticipantIds(),
            request.getCommissionerParticipantIds()
        );
        
        return ResponseEntity.ok(updatedMeetingMinute);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingMinute(@PathVariable Long id) {
        meetingMinuteService.deleteMeetingMinute(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/committee-members")
    public ResponseEntity<List<CommitteeMembers>> getAllCommitteeMembers() {
        return ResponseEntity.ok(meetingMinuteService.getAllCommitteeMembers());
    }
    
    @GetMapping("/commissioner-generals")
    public ResponseEntity<List<CommissionerGeneral>> getAllCommissionerGenerals() {
        return ResponseEntity.ok(meetingMinuteService.getAllCommissionerGenerals());
    }
    
    @GetMapping("/by-type/{meetingType}")
    public ResponseEntity<List<MeetingMinute>> getMeetingMinutesByMeetingType(
            @PathVariable MeetingMinute.MeetingType meetingType) {
        return ResponseEntity.ok(meetingMinuteService.getMeetingMinutesByMeetingType(meetingType));
    }
}