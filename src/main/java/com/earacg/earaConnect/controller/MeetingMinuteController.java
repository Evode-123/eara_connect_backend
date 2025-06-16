package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.dto.MeetingMinuteRequest;
import com.earacg.earaConnect.dto.ResolutionRequest;
import com.earacg.earaConnect.model.AgendaItem;
import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.model.CommitteeMembers;
import com.earacg.earaConnect.model.MeetingMinute;
import com.earacg.earaConnect.service.InvitationService;
import com.earacg.earaConnect.service.MeetingMinuteService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/meeting-minutes")
@RequiredArgsConstructor
public class MeetingMinuteController {

    private final MeetingMinuteService meetingMinuteService;
    private final InvitationService invitationService;

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

    // FIXED: Remove @PathVariable Long userId since we'll get it from the request body
    @PostMapping("/create-basic")
    public ResponseEntity<MeetingMinute> createBasicMeetingMinute(
            @RequestBody MeetingMinuteRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = "";
        
        if (authentication != null && !authentication.getAuthorities().isEmpty()) {
            String authority = authentication.getAuthorities().iterator().next().getAuthority();
            userRole = authority;
            System.out.println("Extracted user role: " + userRole);
        }
        
        // Log the request for debugging
        System.out.println("Create basic meeting request received:");
        System.out.println("User ID from request: " + request.getUserId());
        System.out.println("Meeting No: " + request.getMeetingNo());
        System.out.println("User role: " + userRole);
        
        MeetingMinute meetingMinute = new MeetingMinute();
        meetingMinute.setMeetingNo(request.getMeetingNo());
        meetingMinute.setDate(request.getDate());
        meetingMinute.setLocation(request.getLocation());
        meetingMinute.setMeetingType(request.getMeetingType());
        meetingMinute.setTheme(request.getTheme());
        
        MeetingMinute savedMeetingMinute = meetingMinuteService.createBasicMeetingMinute(
            meetingMinute,
            request.getUserId(), // Use userId from request body
            userRole
        );
        
        return ResponseEntity.ok(savedMeetingMinute);
    }
    
    // Fixed sendInvitations endpoint in MeetingMinuteController.java
    @PostMapping("/{meetingId}/send-invitations")
    public ResponseEntity<Void> sendInvitations(
            @PathVariable Long meetingId,
            @RequestParam("documents") List<MultipartFile> documents,
            @RequestParam(value = "committeeParticipantIds", required = false) List<Long> committeeParticipantIds,
            @RequestParam(value = "commissionerParticipantIds", required = false) List<Long> commissionerParticipantIds) {
        
        // Add logging for debugging
        System.out.println("Meeting ID: " + meetingId);
        System.out.println("Committee participant IDs: " + committeeParticipantIds);
        System.out.println("Commissioner participant IDs: " + commissionerParticipantIds);
        System.out.println("Documents count: " + (documents != null ? documents.size() : 0));
        
        // Handle null lists
        if (committeeParticipantIds == null) {
            committeeParticipantIds = new ArrayList<>();
        }
        if (commissionerParticipantIds == null) {
            commissionerParticipantIds = new ArrayList<>();
        }
        
        invitationService.sendInvitations(
            meetingId,
            committeeParticipantIds,
            commissionerParticipantIds,
            documents
        );
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{meetingId}/add-agenda")
    public ResponseEntity<MeetingMinute> addAgendaItems(
            @PathVariable Long meetingId,
            @RequestBody List<AgendaItem> agendaItems) {
        
        MeetingMinute updated = meetingMinuteService.addAgendaItems(meetingId, agendaItems);
        return ResponseEntity.ok(updated);
    }
    
    @PostMapping("/{meetingId}/add-resolutions")
    public ResponseEntity<MeetingMinute> addResolutions(
            @PathVariable Long meetingId,
            @RequestBody List<ResolutionRequest> resolutions) {
        
        MeetingMinute updated = meetingMinuteService.addResolutions(meetingId, resolutions);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MeetingMinute>> getUpcomingMeetings() {
        return ResponseEntity.ok(meetingMinuteService.getUpcomingMeetings());
    }

    @GetMapping("/all-meetings")
    public ResponseEntity<List<MeetingMinute>> getAllMeetings() {
        return ResponseEntity.ok(meetingMinuteService.findAllMeetingMinutes());
    }
    
   @GetMapping("/documents/download")
    public ResponseEntity<Resource> downloadDocument(
            @RequestParam String filePath,
            Authentication authentication) {
        
        // Verify authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("You must be logged in to download documents");
        }
        
        Resource resource = invitationService.downloadDocument(filePath);

        // Extract filename from path
        String filename = filePath.substring(filePath.lastIndexOf('/') + 1);
        
        // Ensure the filename has the correct extension
        if (!filename.toLowerCase().endsWith(".pdf")) {
            filename += ".pdf";
        }
        
        // Determine content type
        String contentType = "application/pdf";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}