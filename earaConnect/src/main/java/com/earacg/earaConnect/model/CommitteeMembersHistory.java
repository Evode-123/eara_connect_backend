package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "committee_members_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitteeMembersHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "current_position_in_your_rra", nullable = false)
    private String currentPositionInYourRRA;
    
    @ManyToOne
    @JoinColumn(name = "position_in_era_id", nullable = false)
    private Position positionInERA;
    
    @Column(name = "appointed_date", nullable = false)
    private Date appointedDate;
    
    @Lob
    @Column(name = "appointed_letter", nullable = false)
    private byte[] appointedLetter;
    
    @Column(name = "appointed_letter_name", nullable = false)
    private String appointedLetterName;
    
    @Column(name = "appointed_letter_type", nullable = false)
    private String appointedLetterType;
    
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}