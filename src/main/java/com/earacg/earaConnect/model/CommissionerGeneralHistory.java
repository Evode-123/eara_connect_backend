package com.earacg.earaConnect.model;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commissioner_general_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommissionerGeneralHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cg_name", nullable = false)
    private String cgName;
    
    @Column(name = "cg_phone", nullable = false)
    private String cgPhone;
    
    @Column(name = "cg_email", nullable = false)
    private String cgEmail;
    
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
    
    @ManyToOne
    @JoinColumn(name = "revenue_authority_id", nullable = false)
    private RevenueAuthority revenueAuthority;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private User.Gender gender;
    
    @Column(name = "job_position")
    private String currentJobPosition;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "employment_date")
    private Date employmentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false)
    private MemberType memberType;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}