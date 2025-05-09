package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "position")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private PositionName positionName;

    public String getDisplayName() {
        return positionName.toString();
    }
    
    public enum PositionName {
        HEAD_OF_DELEGATION("Head Of Delegation"),
        SECRETARY("Secretary"),
        DOMESTIC_REVENUE_SUB_COMMITTEE("Domestic Revenue Sub Committee"),
        CUSTOMS_REVENUE_SUB_COMMITTEE("Customs Revenue Sub Committee"),
        IT_SUB_COMMITTEE("IT Sub Committee"),
        HR_SUB_COMMITTEE("HR Sub Committee"),
        RESEARCH_AND_PLANNING_SUB_COMMITTEE("Research and Planning Sub Committee"),
        LEGAL_SUB_COMMITTEE("Legal Sub Committee");

        private final String displayName;

        PositionName(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}