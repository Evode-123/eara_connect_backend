package com.earacg.earaConnect.service;

import com.earacg.earaConnect.dto.ResolutionRequest;
import com.earacg.earaConnect.model.*;
import com.earacg.earaConnect.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResolutionService {
    private final ResolutionRepository resolutionRepository;
    private final ResolutionAssignmentRepository resolutionAssignmentRepository;
    private final MeetingMinuteRepository meetingMinuteRepository;
    private final CountryRepository countryRepository;
    private final PositionRepository positionRepository;

    public List<Resolution> getResolutionsByMeetingMinute(Long meetingMinuteId) {
        MeetingMinute meetingMinute = meetingMinuteRepository.findById(meetingMinuteId)
                .orElseThrow(() -> new RuntimeException("Meeting Minute not found with id: " + meetingMinuteId));
        return resolutionRepository.findByMeetingMinute(meetingMinute);
    }

    public Resolution getResolutionById(Long id) {
        return resolutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resolution not found with id: " + id));
    }

    @Transactional
    public Resolution createResolution(Long meetingMinuteId, ResolutionRequest request) {
        MeetingMinute meetingMinute = meetingMinuteRepository.findById(meetingMinuteId)
                .orElseThrow(() -> new RuntimeException("Meeting Minute not found with id: " + meetingMinuteId));
        
        Resolution resolution = new Resolution();
        resolution.setTitle(request.getTitle());
        resolution.setDescription(request.getDescription());
        resolution.setDeadlineDate(request.getDeadlineDate());
        resolution.setStatus(request.getStatus() != null ? request.getStatus() : Resolution.ResolutionStatus.PENDING);
        resolution.setMeetingMinute(meetingMinute);
        
        // Save the resolution first
        Resolution savedResolution = resolutionRepository.save(resolution);
        
        // Create assignments based on the request
        createAssignments(savedResolution, request);
        
        return savedResolution;
    }

    private void createAssignments(Resolution resolution, ResolutionRequest request) {
        List<ResolutionAssignment> assignments = new ArrayList<>();
        
        if (request.getAssigneeType() == Resolution.AssigneeType.COUNTRY && request.getAssignedCountryIds() != null) {
            // Create an assignment for each country
            List<Country> countries = countryRepository.findAllById(request.getAssignedCountryIds());
            for (Country country : countries) {
                ResolutionAssignment assignment = new ResolutionAssignment();
                assignment.setResolution(resolution);
                assignment.setAssigneeType(Resolution.AssigneeType.COUNTRY);
                assignment.setAssignedCountry(country);
                assignments.add(assignment);
            }
        } else if (request.getAssigneeType() == Resolution.AssigneeType.POSITION && request.getAssignedPositionIds() != null) {
            // Create an assignment for each position
            List<Position> positions = positionRepository.findAllById(request.getAssignedPositionIds());
            for (Position position : positions) {
                ResolutionAssignment assignment = new ResolutionAssignment();
                assignment.setResolution(resolution);
                assignment.setAssigneeType(Resolution.AssigneeType.POSITION);
                assignment.setAssignedPosition(position);
                assignments.add(assignment);
            }
        } else if (request.getAssigneeType() == Resolution.AssigneeType.ALL_COMMISSIONERS) {
            // Create a single assignment for all commissioners
            ResolutionAssignment assignment = new ResolutionAssignment();
            assignment.setResolution(resolution);
            assignment.setAssigneeType(Resolution.AssigneeType.ALL_COMMISSIONERS);
            assignment.setAssignedToAllCommissioners(true);
            assignments.add(assignment);
        }
        
        // Save all assignments
        resolutionAssignmentRepository.saveAll(assignments);
    }

    @Transactional
    public Resolution updateResolution(Long id, ResolutionRequest request) {
        Resolution existingResolution = getResolutionById(id);
        
        existingResolution.setTitle(request.getTitle());
        existingResolution.setDescription(request.getDescription());
        existingResolution.setDeadlineDate(request.getDeadlineDate());
        existingResolution.setStatus(request.getStatus());
        
        // Clear existing assignments
        List<ResolutionAssignment> existingAssignments = resolutionAssignmentRepository.findByResolution(existingResolution);
        resolutionAssignmentRepository.deleteAll(existingAssignments);
        
        // Create new assignments
        createAssignments(existingResolution, request);
        
        return resolutionRepository.save(existingResolution);
    }

    @Transactional
    public Resolution updateResolutionStatus(Long id, Resolution.ResolutionStatus status) {
        Resolution existingResolution = getResolutionById(id);
        existingResolution.setStatus(status);
        return resolutionRepository.save(existingResolution);
    }

    @Transactional
    public void deleteResolution(Long id) {
        resolutionRepository.deleteById(id);
    }
    
    public List<Resolution> getResolutionsAssignedToCountry(Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
        
        List<ResolutionAssignment> assignments = resolutionAssignmentRepository
                .findByAssigneeTypeAndAssignedCountry(Resolution.AssigneeType.COUNTRY, country);
        
        return assignments.stream()
                .map(ResolutionAssignment::getResolution)
                .distinct()
                .collect(Collectors.toList());
    }
    
    public List<Resolution> getResolutionsAssignedToPosition(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + positionId));
        
        List<ResolutionAssignment> assignments = resolutionAssignmentRepository
                .findByAssigneeTypeAndAssignedPosition(Resolution.AssigneeType.POSITION, position);
        
        return assignments.stream()
                .map(ResolutionAssignment::getResolution)
                .distinct()
                .collect(Collectors.toList());
    }
    
    public List<Resolution> getResolutionsAssignedToAllCommissioners() {
        List<ResolutionAssignment> assignments = resolutionAssignmentRepository
                .findByAssigneeTypeAndAssignedToAllCommissionersTrue(Resolution.AssigneeType.ALL_COMMISSIONERS);
        
        return assignments.stream()
                .map(ResolutionAssignment::getResolution)
                .distinct()
                .collect(Collectors.toList());
    }
}