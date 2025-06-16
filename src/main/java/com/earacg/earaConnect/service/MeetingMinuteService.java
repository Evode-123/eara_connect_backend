package com.earacg.earaConnect.service;

import com.earacg.earaConnect.dto.ResolutionRequest;
import com.earacg.earaConnect.model.*;
import com.earacg.earaConnect.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingMinuteService {
    private final MeetingMinuteRepository meetingMinuteRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final CommitteeMembersRepository committeeMembersRepository;
    private final CommissionerGeneralRepository commissionerGeneralRepository;
    private final ResolutionService resolutionService;
    private final AdminRepository adminRepository;

    public List<MeetingMinute> getAllMeetingMinutes() {
        return meetingMinuteRepository.findAll();
    }

    public MeetingMinute getMeetingMinuteById(Long id) {
        return meetingMinuteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting Minute not found with id: " + id));
    }

    @Transactional
    public MeetingMinute createMeetingMinute(MeetingMinute meetingMinute, 
                                              Long createdById,
                                              List<Long> committeeParticipantIds,
                                              List<Long> commissionerParticipantIds,
                                              List<AgendaItem> agendaItems,
                                              List<ResolutionRequest> resolutions,
                                              String creatorRole) {
        // Check if the role contains COMMISSIONER_GENERAL (to handle cases like "ROLE_COMMISSIONER_GENERAL")
        boolean isCommissionerRole = creatorRole != null && creatorRole.contains("COMMISSIONER_GENERAL");
        
        // Set created by based on role
        if (isCommissionerRole) {
            // First try to find the commissioner
            CommissionerGeneral createdBy = commissionerGeneralRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("Commissioner General not found with id: " + createdById));
            meetingMinute.setCreatedByCommissioner(createdBy);
            // Clear the committee creator if it was set
            meetingMinute.setCreatedBy(null);
        } else {
            // First try to find the committee member
            CommitteeMembers createdBy = committeeMembersRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("Committee member not found with id: " + createdById));
            meetingMinute.setCreatedBy(createdBy);
            // Clear the commissioner creator if it was set
            meetingMinute.setCreatedByCommissioner(null);
        }
        
        // Set committee participants
        List<CommitteeMembers> committeeParticipants = new ArrayList<>();
        if (committeeParticipantIds != null && !committeeParticipantIds.isEmpty()) {
            committeeParticipants = committeeMembersRepository.findAllById(committeeParticipantIds);
        }
        meetingMinute.setCommitteeParticipants(committeeParticipants);
        
        // Set commissioner participants
        List<CommissionerGeneral> commissionerParticipants = new ArrayList<>();
        if (commissionerParticipantIds != null && !commissionerParticipantIds.isEmpty()) {
            commissionerParticipants = commissionerGeneralRepository.findAllById(commissionerParticipantIds);
        }
        meetingMinute.setCommissionerParticipants(commissionerParticipants);
        
        // Save meeting minute
        MeetingMinute savedMeetingMinute = meetingMinuteRepository.save(meetingMinute);
        
        // Save agenda items
        if (agendaItems != null && !agendaItems.isEmpty()) {
            int order = 1;
            for (AgendaItem agendaItem : agendaItems) {
                agendaItem.setMeetingMinute(savedMeetingMinute);
                agendaItem.setDisplayOrder(order++);
                agendaItemRepository.save(agendaItem);
            }
        }
        
        // Create resolutions
        if (resolutions != null && !resolutions.isEmpty()) {
            for (ResolutionRequest resolutionRequest : resolutions) {
                resolutionService.createResolution(savedMeetingMinute.getId(), resolutionRequest);
            }
        }
        
        return savedMeetingMinute;
    }

    // Other methods remain unchanged
    @Transactional
    public MeetingMinute updateMeetingMinute(Long id, MeetingMinute meetingMinuteDetails,
                                            List<Long> committeeParticipantIds,
                                            List<Long> commissionerParticipantIds) {
        MeetingMinute existingMeetingMinute = getMeetingMinuteById(id);
        
        // Update basic details
        existingMeetingMinute.setMeetingNo(meetingMinuteDetails.getMeetingNo());
        existingMeetingMinute.setDate(meetingMinuteDetails.getDate());
        existingMeetingMinute.setLocation(meetingMinuteDetails.getLocation());
        existingMeetingMinute.setMeetingType(meetingMinuteDetails.getMeetingType());
        existingMeetingMinute.setTheme(meetingMinuteDetails.getTheme());
        
        // Update committee participants
        if (committeeParticipantIds != null) {
            List<CommitteeMembers> committeeParticipants = committeeMembersRepository.findAllById(committeeParticipantIds);
            existingMeetingMinute.setCommitteeParticipants(committeeParticipants);
        }
        
        // Update commissioner participants
        if (commissionerParticipantIds != null) {
            List<CommissionerGeneral> commissionerParticipants = commissionerGeneralRepository.findAllById(commissionerParticipantIds);
            existingMeetingMinute.setCommissionerParticipants(commissionerParticipants);
        }
        
        return meetingMinuteRepository.save(existingMeetingMinute);
    }

    @Transactional
    public void deleteMeetingMinute(Long id) {
        meetingMinuteRepository.deleteById(id);
    }
    
    public List<CommitteeMembers> getAllCommitteeMembers() {
        return committeeMembersRepository.findAll();
    }
    
    public List<CommissionerGeneral> getAllCommissionerGenerals() {
        return commissionerGeneralRepository.findAll();
    }
    
    public List<MeetingMinute> getMeetingMinutesByMeetingType(MeetingMinute.MeetingType meetingType) {
        return meetingMinuteRepository.findByMeetingType(meetingType);
    }

    @Transactional
    public MeetingMinute createBasicMeetingMinute(MeetingMinute meetingMinute, 
                                                Long createdById,
                                                String creatorRole) {
        if (creatorRole == null || createdById == null) {
            throw new IllegalArgumentException("Creator role and ID cannot be null");
        }

        try {
            switch (creatorRole) {
                case "COMMISSIONER_GENERAL":
                    CommissionerGeneral commissioner = commissionerGeneralRepository.findById(createdById)
                        .orElseThrow(() -> new RuntimeException("Commissioner not found with id: " + createdById));
                    meetingMinute.setCreatedByCommissioner(commissioner);
                    meetingMinute.setCreatedBy(null);
                    meetingMinute.setCreatedByAdmin(null);
                    break;
                    
                case "ADMIN":
                    // Assuming you have an AdminRepository and Admin entity
                    Admin admin = adminRepository.findById(createdById)
                     .orElseThrow(() -> new RuntimeException("Admin not found with id: " + createdById));
                      meetingMinute.setCreatedByAdmin(admin);
                      meetingMinute.setCreatedBy(null);
                      meetingMinute.setCreatedByCommissioner(null);
                      break;
                    // If you don't have admin-specific tracking, fall through to committee member
                    
                default: // For COMMITTEE_MEMBER and any other roles
                    CommitteeMembers member = committeeMembersRepository.findById(createdById)
                        .orElseThrow(() -> new RuntimeException("Committee member not found with id: " + createdById));
                    meetingMinute.setCreatedBy(member);
                    meetingMinute.setCreatedByCommissioner(null);
                    meetingMinute.setCreatedByAdmin(null);
                    break;
            }
            
            return meetingMinuteRepository.save(meetingMinute);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create meeting minute: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public MeetingMinute addAgendaItems(Long meetingId, List<AgendaItem> agendaItems) {
        MeetingMinute meeting = getMeetingMinuteById(meetingId);
        
        if (agendaItems != null && !agendaItems.isEmpty()) {
            int order = 1;
            for (AgendaItem agendaItem : agendaItems) {
                agendaItem.setMeetingMinute(meeting);
                agendaItem.setDisplayOrder(order++);
                agendaItemRepository.save(agendaItem);
            }
        }
        
        return meeting;
    }
    
    @Transactional
    public MeetingMinute addResolutions(Long meetingId, List<ResolutionRequest> resolutions) {
        MeetingMinute meeting = getMeetingMinuteById(meetingId);
        
        if (resolutions != null && !resolutions.isEmpty()) {
            for (ResolutionRequest resolutionRequest : resolutions) {
                resolutionService.createResolution(meetingId, resolutionRequest);
            }
        }
        
        return meeting;
    }

    public List<MeetingMinute> getUpcomingMeetings() {
        LocalDateTime now = LocalDateTime.now();
        return meetingMinuteRepository.findByDateAfterAndStatusNot(
            now, 
            MeetingMinute.MeetingStatus.COMPLETED
        );
    }

    public List<MeetingMinute> findAllMeetingMinutes(){
        return meetingMinuteRepository.findAll();
    }
}