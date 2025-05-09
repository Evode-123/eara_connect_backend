package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "country_name", nullable = false)
    private CountryName countryName;
    
    @ManyToOne
    @JoinColumn(name = "eac_id", nullable = false)
    private Eac eac;
    
    public enum CountryName {
        RWANDA("Rwanda"),
        UGANDA("Uganda"),
        KENYA("Kenya"),
        TANZANIA("Tanzania"),
        ZANZIBAR("Zanzibar"),
        BURUNDI("Burundi"),
        SOUTH_SUDAN("South Sudan");

        private final String displayName;

        CountryName(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}