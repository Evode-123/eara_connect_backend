package com.earacg.earaConnect.dto;

import com.earacg.earaConnect.model.AgendaItem;
import com.earacg.earaConnect.model.MeetingMinute;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MeetingMinuteRequest {
    private String meetingNo;
    private LocalDate date;
    private String location;
    private MeetingMinute.MeetingType meetingType;
    private String theme;
    private List<Long> committeeParticipantIds;
    private List<Long> commissionerParticipantIds;
    private List<AgendaItem> agendaItems;
    private List<ResolutionRequest> resolutions;
    private String userRole; // Added field to pass role explicitly if needed
}