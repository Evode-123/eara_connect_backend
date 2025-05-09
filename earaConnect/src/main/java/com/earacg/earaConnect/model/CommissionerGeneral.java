package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commissioner_general")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CommissionerGeneral {
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
}
