package com.earacg.earaConnect.dto;

import com.earacg.earaConnect.model.Resolution;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResolutionRequest {
    private String title;
    private String description;
    private LocalDate deadlineDate;
    private Resolution.ResolutionStatus status = Resolution.ResolutionStatus.PENDING;
    private Resolution.AssigneeType assigneeType;
    private List<Long> assignedCountryIds;   // Modified to support multiple countries
    private List<Long> assignedPositionIds;  // Modified to support multiple positions
    private Boolean assignToAllCommissioners = false;
}