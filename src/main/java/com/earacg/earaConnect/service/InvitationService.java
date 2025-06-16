package com.earacg.earaConnect.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.model.CommitteeMembers;
import com.earacg.earaConnect.model.MeetingDocument;
import com.earacg.earaConnect.model.MeetingMinute;
import com.earacg.earaConnect.repository.MeetingMinuteRepository;
import com.earacg.earaConnect.repository.MeetingDocumentRepository;
import com.earacg.earaConnect.repository.CommitteeMembersRepository;
import com.earacg.earaConnect.repository.CommissionerGeneralRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationService {
    private final JavaMailSender mailSender;
    private final MeetingMinuteRepository meetingMinuteRepository;
    private final MeetingDocumentRepository meetingDocumentRepository;
    private final CommitteeMembersRepository committeeMembersRepository;
    private final CommissionerGeneralRepository commissionerGeneralRepository;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Transactional
    public void sendInvitations(Long meetingId, List<Long> committeeParticipantIds, 
                              List<Long> commissionerParticipantIds, 
                              List<MultipartFile> documents) {
        MeetingMinute meeting = meetingMinuteRepository.findById(meetingId)
            .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        // Save documents first
        List<MeetingDocument> savedDocuments = new ArrayList<>();
        for (MultipartFile file : documents) {
            try {
                String uploadDir = "meeting-documents/" + meetingId;
                String fileName = storeFile(file, uploadDir);
                
                MeetingDocument doc = new MeetingDocument();
                doc.setName(file.getOriginalFilename());
                doc.setFilePath(uploadDir + "/" + fileName);
                doc.setFileType(file.getContentType());
                doc.setMeetingMinute(meeting);
                
                savedDocuments.add(meetingDocumentRepository.save(doc));
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }
        
        // Get all participants emails
        List<String> emails = new ArrayList<>();
        
        if (committeeParticipantIds != null && !committeeParticipantIds.isEmpty()) {
            List<CommitteeMembers> committeeMembers = committeeMembersRepository.findAllById(committeeParticipantIds);
            emails.addAll(committeeMembers.stream()
                .map(CommitteeMembers::getEmail)
                .collect(Collectors.toList()));
        }
        
        if (commissionerParticipantIds != null && !commissionerParticipantIds.isEmpty()) {
            List<CommissionerGeneral> commissioners = commissionerGeneralRepository.findAllById(commissionerParticipantIds);
            emails.addAll(commissioners.stream()
                .map(CommissionerGeneral::getEmail)
                .collect(Collectors.toList()));
        }
        
        // Send emails
        for (String email : emails) {
            sendInvitationEmail(email, meeting, savedDocuments);
        }
        
        // Update meeting status
        meeting.setStatus(MeetingMinute.MeetingStatus.INVITED);
        meetingMinuteRepository.save(meeting);
    }
    
    private String storeFile(MultipartFile file, String uploadDir) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(uniqueFileName), 
                      StandardCopyOption.REPLACE_EXISTING);
        }
        
        return uniqueFileName;
    }
    
    private void sendInvitationEmail(String email, MeetingMinute meeting, List<MeetingDocument> documents) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(email);
            helper.setSubject("Invitation to Meeting: " + meeting.getTheme());
            
            // Create email content
            String content = buildEmailContent(meeting, documents);
            helper.setText(content, true);
            
            // Add document attachments if needed
            for (MeetingDocument doc : documents) {
                FileSystemResource file = new FileSystemResource(doc.getFilePath());
                helper.addAttachment(doc.getName(), file);
            }
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private String buildEmailContent(MeetingMinute meeting, List<MeetingDocument> documents) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>Meeting Invitation</h2>");
        sb.append("<p>You are invited to attend the following meeting:</p>");
        sb.append("<ul>");
        sb.append("<li><strong>Meeting No:</strong> ").append(meeting.getMeetingNo()).append("</li>");
        sb.append("<li><strong>Date:</strong> ").append(meeting.getDate()).append("</li>");
        sb.append("<li><strong>Location:</strong> ").append(meeting.getLocation()).append("</li>");
        sb.append("<li><strong>Type:</strong> ").append(meeting.getMeetingType()).append("</li>");
        sb.append("<li><strong>Theme:</strong> ").append(meeting.getTheme()).append("</li>");
        sb.append("</ul>");
        
        if (!documents.isEmpty()) {
            sb.append("<h3>Attached Documents:</h3>");
            sb.append("<ul>");
            for (MeetingDocument doc : documents) {
                sb.append("<li>").append(doc.getName()).append("</li>");
            }
            sb.append("</ul>");
        }
        
        sb.append("<p>Please confirm your attendance.</p>");
        sb.append("</body></html>");
        
        return sb.toString();
    }

    public Resource downloadDocument(String filePath) {
        MeetingDocument document = meetingDocumentRepository.findByFilePath(filePath)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        Path path = Paths.get(filePath);
        Resource resource = new FileSystemResource(path);
        
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read file");
        }
    }
    
}