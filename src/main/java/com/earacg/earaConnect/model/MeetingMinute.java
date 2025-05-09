package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Table(name = "meeting_minute")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingMinute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "meeting_no", nullable = false)
    private String meetingNo;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    private MeetingType meetingType;
    
    @Column(nullable = false)
    private String theme;
    
    @ManyToMany
    @JoinTable(
        name = "meeting_committee_participants",
        joinColumns = @JoinColumn(name = "meeting_minute_id"),
        inverseJoinColumns = @JoinColumn(name = "committee_member_id")
    )
    private List<CommitteeMembers> committeeParticipants = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "meeting_commissioner_participants",
        joinColumns = @JoinColumn(name = "meeting_minute_id"),
        inverseJoinColumns = @JoinColumn(name = "commissioner_id")
    )
    private List<CommissionerGeneral> commissionerParticipants = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private CommitteeMembers createdBy;

    @ManyToOne
    @JoinColumn(name = "created_by_commissioner_id")
    private CommissionerGeneral createdByCommissioner;
    
    @OneToMany(mappedBy = "meetingMinute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgendaItem> agendaItems = new ArrayList<>();
    
    @OneToMany(mappedBy = "meetingMinute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resolution> resolutions = new ArrayList<>();
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    
    public enum MeetingType {
        COMMISSIONER_GENERAL_MEETING("Commissioner General Meeting"),
        DOMESTIC_REVENUE_COMMITTEE("Domestic Revenue Committee"),
        CUSTOMS_REVENUE_COMMITTEE("Customs Revenue Committee"),
        IT_COMMITTEE("IT Committee"),
        HR_COMMITTEE("HR Committee"),
        RESEARCH_AND_PLANNING_COMMITTEE("Research and Planning Committee"),
        LEGAL_COMMITTEE("Legal Committee");
        
        private final String displayName;
        
        MeetingType(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}