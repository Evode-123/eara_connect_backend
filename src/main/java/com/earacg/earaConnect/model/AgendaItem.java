package com.earacg.earaConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agenda_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgendaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @ManyToOne
    @JoinColumn(name = "meeting_minute_id", nullable = false)
    private MeetingMinute meetingMinute;
}