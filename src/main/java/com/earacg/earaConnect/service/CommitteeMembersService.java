package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.*;
import com.earacg.earaConnect.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommitteeMembersService {
    private final CommitteeMembersRepository membersRepository;
    private final CommitteeMembersHistoryRepository historyRepository;
    private final CountryRepository countryRepository;
    private final PositionRepository positionRepository;
    private final RevenueAuthorityRepository revenueAuthorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserService userService;

    public List<CommitteeMembers> getAllCommitteeMembers() {
        return membersRepository.findAll();
    }

    public CommitteeMembers getMemberById(Long id) {
        return membersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Committee member not found with id: " + id));
    }

    @Transactional
    public CommitteeMembers createMember(CommitteeMembers member, Long countryId, Long positionId,
                                    Long revenueAuthorityId, MemberType memberType,
                                    MultipartFile appointedLetter) throws IOException {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + positionId));
        RevenueAuthority revenueAuthority = revenueAuthorityRepository.findById(revenueAuthorityId)
                .orElseThrow(() -> new RuntimeException("Revenue Authority not found with id: " + revenueAuthorityId));
        
        // Debug logging
        System.out.println("Creating committee member: " + member.getName());
        System.out.println("Email: " + member.getEmail());
        System.out.println("Phone: " + member.getPhone());
        System.out.println("Position: " + position.getDisplayName());
        System.out.println("Member Type: " + memberType);
        
        member.setCountry(country);
        member.setPositionInERA(position);
        member.setRevenueAuthority(revenueAuthority);
        member.setMemberType(memberType);
        member.setAppointedLetter(appointedLetter.getBytes());
        member.setAppointedLetterName(appointedLetter.getOriginalFilename());
        member.setAppointedLetterType(appointedLetter.getContentType());
        
        // Generate a random password
        String rawPassword = userService.generateRandomPassword();
        member.setPassword(passwordEncoder.encode(rawPassword));
        
        // Set first login flag
        member.setFirstLogin(true);
        
        // Set the role
        member.setRole("COMMITTEE_MEMBER");
        
        // Save the member
        CommitteeMembers savedMember = membersRepository.save(member);
        
        // Log the credentials that will be sent
        System.out.println("Committee Member credentials created:");
        System.out.println("Email: " + member.getEmail());
        System.out.println("Password: " + rawPassword);
        
        // Send credentials via email
        emailService.sendCredentials(member.getEmail(), rawPassword);
        
        return savedMember;
    }

    @Transactional
    public CommitteeMembers updateMember(Long id, CommitteeMembers memberDetails, 
                                       MemberType memberType,
                                       MultipartFile appointedLetter) throws IOException {
        CommitteeMembers existing = getMemberById(id);
        
        // Save to history before updating
        saveToHistory(existing);
        
        existing.setName(memberDetails.getName());
        existing.setPhone(memberDetails.getPhone());
        existing.setEmail(memberDetails.getEmail());
        existing.setCurrentPositionInYourRRA(memberDetails.getCurrentPositionInYourRRA());
        existing.setAppointedDate(memberDetails.getAppointedDate());
        
        // Update member type if changed
        if (memberType != null) {
            existing.setMemberType(memberType);
        }
        
        if (appointedLetter != null && !appointedLetter.isEmpty()) {
            existing.setAppointedLetter(appointedLetter.getBytes());
            existing.setAppointedLetterName(appointedLetter.getOriginalFilename());
            existing.setAppointedLetterType(appointedLetter.getContentType());
        }
        
        return membersRepository.save(existing);
    }

    @Transactional
    public CommitteeMembers updateMemberProfile(Long id, User.Gender gender, String jobPosition, 
                                              String department, java.util.Date employmentDate) {
        CommitteeMembers existing = getMemberById(id);
        
        // Save to history before updating
        saveToHistory(existing);
        
        existing.setGender(gender);
        existing.setCurrentJobPosition(jobPosition);
        existing.setDepartment(department);
        existing.setEmploymentDate(employmentDate);
        
        return membersRepository.save(existing);
    }

    private void saveToHistory(CommitteeMembers member) {
        CommitteeMembersHistory history = new CommitteeMembersHistory();
        history.setName(member.getName());
        history.setPhone(member.getPhone());
        history.setEmail(member.getEmail());
        history.setCurrentPositionInYourRRA(member.getCurrentPositionInYourRRA());
        history.setPositionInERA(member.getPositionInERA());
        history.setAppointedDate(member.getAppointedDate());
        history.setAppointedLetter(member.getAppointedLetter());
        history.setAppointedLetterName(member.getAppointedLetterName());
        history.setAppointedLetterType(member.getAppointedLetterType());
        history.setCountry(member.getCountry());
        history.setRevenueAuthority(member.getRevenueAuthority());
        history.setMemberType(member.getMemberType());
        history.setGender(member.getGender());
        history.setCurrentJobPosition(member.getCurrentJobPosition());
        history.setDepartment(member.getDepartment());
        history.setEmploymentDate(member.getEmploymentDate());
        history.setUpdatedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Transactional
    public void deleteMember(Long id) {
        membersRepository.deleteById(id);
    }

    public byte[] getAppointedLetter(Long memberId) {
        CommitteeMembers member = getMemberById(memberId);
        return member.getAppointedLetter();
    }

    public String getAppointedLetterName(Long memberId) {
        CommitteeMembers member = getMemberById(memberId);
        return member.getAppointedLetterName();
    }

    public String getAppointedLetterType(Long memberId) {
        CommitteeMembers member = getMemberById(memberId);
        return member.getAppointedLetterType();
    }

    public List<CommitteeMembers> getMembersByCountryId(Long countryId) {
        return membersRepository.findByCountryId(countryId);
    }
}