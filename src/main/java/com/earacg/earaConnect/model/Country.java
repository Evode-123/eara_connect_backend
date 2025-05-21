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

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "iso_code", nullable = false)
    private String isoCode; // New field for ISO 2-letter code

    @ManyToOne
    @JoinColumn(name = "eac_id", nullable = false)
    private Eac eac;
}