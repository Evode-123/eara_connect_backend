package com.earacg.earaConnect.dto;

import com.earacg.earaConnect.model.AgendaItem;
import com.earacg.earaConnect.model.MeetingMinute;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingMinuteRequest {
    private Long userId; // Add this field
    private String meetingNo;
    private LocalDateTime date;
    private String location;
    private MeetingMinute.MeetingType meetingType;
    private String theme;
    private List<Long> committeeParticipantIds;
    private List<Long> commissionerParticipantIds;
    private List<AgendaItem> agendaItems;
    private List<ResolutionRequest> resolutions;
    private String userRole;
}