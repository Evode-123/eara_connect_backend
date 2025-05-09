package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "eac")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Eac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private EacName eacName;
    
    public enum EacName {
        EARACG("EARACG");

        private final String displayName;

        EacName(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}