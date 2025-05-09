package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commissioner_general")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommissionerGeneral extends User {
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
    @Column(name = "member_type", nullable = false)
    private MemberType memberType;

    @Override
    public String getCurrentJobPosition() {
        // Either return the explicitly set position or default to "Commissioner General"
        return super.getCurrentJobPosition() == null ? "Commissioner General" : super.getCurrentJobPosition();
    }

    @Override
    public String getRole() {
        return "COMMISSIONER_GENERAL";
    }
}