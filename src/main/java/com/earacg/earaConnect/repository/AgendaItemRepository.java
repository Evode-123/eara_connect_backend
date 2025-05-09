package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.AgendaItem;
import com.earacg.earaConnect.model.MeetingMinute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {
    List<AgendaItem> findByMeetingMinute(MeetingMinute meetingMinute);
    
    List<AgendaItem> findByMeetingMinuteOrderByDisplayOrderAsc(MeetingMinute meetingMinute);
}