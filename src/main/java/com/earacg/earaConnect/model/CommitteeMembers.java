package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "committee_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitteeMembers extends User {
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
    
    @ManyToOne
    @JoinColumn(name = "revenue_authority_id", nullable = false)
    private RevenueAuthority revenueAuthority;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false)
    private MemberType memberType;

    @Override
    public String getRole() {
        return "COMMITTEE_MEMBER";
    }

    public CommitteeMembers(Long id, String name, String phone, String email, 
                      String currentPositionInYourRRA, Position positionInERA, 
                      Date appointedDate, String appointedLetterName, 
                      String appointedLetterType, Country country, 
                      RevenueAuthority revenueAuthority, MemberType memberType,
                      String password, boolean firstLogin, Gender gender,
                      String currentJobPosition, String department, Date employmentDate) {
    // Initialize parent class fields
    this.setEmail(email);
    this.setPassword(password);
    this.setFirstLogin(firstLogin);
    this.setGender(gender);
    this.setCurrentJobPosition(currentJobPosition);
    this.setDepartment(department);
    this.setEmploymentDate(employmentDate);
    this.setRole("COMMITTEE_MEMBER");
    
    // Initialize child class fields
    this.id = id;
    this.name = name;
    this.phone = phone;
    this.currentPositionInYourRRA = currentPositionInYourRRA;
    this.positionInERA = positionInERA;
    this.appointedDate = appointedDate;
    this.appointedLetterName = appointedLetterName;
    this.appointedLetterType = appointedLetterType;
    this.country = country;
    this.revenueAuthority = revenueAuthority;
    this.memberType = memberType;
}
}