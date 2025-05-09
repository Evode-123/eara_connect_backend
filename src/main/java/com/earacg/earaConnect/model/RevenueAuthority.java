package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "revenue_authority")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorityName authorityName;
    
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
    
    public enum AuthorityName {
        RWANDA_REVENUE_AUTHORITY("Rwanda Revenue Authority"),
        UGANDA_REVENUE_AUTHORITY("Uganda Revenue Authority"),
        KENYA_REVENUE_AUTHORITY("Kenya Revenue Authority"),
        TANZANIA_REVENUE_AUTHORITY("Tanzania Revenue Authority"),
        ZANZIBAR_REVENUE_AUTHORITY("Zanzibar Revenue Authority"),
        OFFICE_BURUNDAIS_DES_RECETTES("Office Burundais des Recettes"),
        SOUTH_SUDAN_REVENUE_AUTHORITY("South Sudan Revenue Authority");

        private final String displayName;

        AuthorityName(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}