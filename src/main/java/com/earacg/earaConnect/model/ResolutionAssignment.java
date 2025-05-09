package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resolution_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResolutionAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "resolution_id", nullable = false)
    private Resolution resolution;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "assignee_type", nullable = false)
    private Resolution.AssigneeType assigneeType;
    
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country assignedCountry;
    
    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position assignedPosition;
    
    @Column(name = "all_commissioners")
    private Boolean assignedToAllCommissioners = false;
}