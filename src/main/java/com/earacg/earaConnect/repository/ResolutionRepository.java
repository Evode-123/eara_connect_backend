package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.Resolution;
import com.earacg.earaConnect.model.MeetingMinute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResolutionRepository extends JpaRepository<Resolution, Long> {
    List<Resolution> findByMeetingMinute(MeetingMinute meetingMinute);
    List<Resolution> findByStatus(Resolution.ResolutionStatus status);
    List<Resolution> findByMeetingMinuteId(Long meetingMinuteId);
}