package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.*;
import com.earacg.earaConnect.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public List<CommitteeMembers> getAllCommitteeMembers() {
        return membersRepository.findAll();
    }

    public CommitteeMembers getMemberById(Long id) {
        return membersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Committee member not found with id: " + id));
    }

    @Transactional
    public CommitteeMembers createMember(CommitteeMembers member, Long countryId, Long positionId, 
                                      MultipartFile appointedLetter) throws IOException {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + positionId));
        
        member.setCountry(country);
        member.setPositionInERA(position);
        member.setAppointedLetter(appointedLetter.getBytes());
        member.setAppointedLetterName(appointedLetter.getOriginalFilename());
        member.setAppointedLetterType(appointedLetter.getContentType());
        
        return membersRepository.save(member);
    }

    @Transactional
    public CommitteeMembers updateMember(Long id, CommitteeMembers memberDetails, 
                                       MultipartFile appointedLetter) throws IOException {
        CommitteeMembers existing = getMemberById(id);
        
        // Save to history before updating
        saveToHistory(existing);
        
        existing.setName(memberDetails.getName());
        existing.setPhone(memberDetails.getPhone());
        existing.setEmail(memberDetails.getEmail());
        existing.setCurrentPositionInYourRRA(memberDetails.getCurrentPositionInYourRRA());
        existing.setAppointedDate(memberDetails.getAppointedDate());
        
        if (appointedLetter != null && !appointedLetter.isEmpty()) {
            existing.setAppointedLetter(appointedLetter.getBytes());
            existing.setAppointedLetterName(appointedLetter.getOriginalFilename());
            existing.setAppointedLetterType(appointedLetter.getContentType());
        }
        
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
}