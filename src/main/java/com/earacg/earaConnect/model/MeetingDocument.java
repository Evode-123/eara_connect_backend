package com.earacg.earaConnect.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meeting_documents")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @ManyToOne
    @JoinColumn(name = "meeting_minute_id")
    private MeetingMinute meetingMinute;
    
    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}