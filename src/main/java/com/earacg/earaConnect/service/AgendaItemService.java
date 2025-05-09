package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.*;
import com.earacg.earaConnect.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaItemService {
    private final AgendaItemRepository agendaItemRepository;
    private final MeetingMinuteRepository meetingMinuteRepository;

    public List<AgendaItem> getAgendaItemsByMeetingMinute(Long meetingMinuteId) {
        MeetingMinute meetingMinute = meetingMinuteRepository.findById(meetingMinuteId)
                .orElseThrow(() -> new RuntimeException("Meeting Minute not found with id: " + meetingMinuteId));
        return agendaItemRepository.findByMeetingMinuteOrderByDisplayOrderAsc(meetingMinute);
    }

    public AgendaItem getAgendaItemById(Long id) {
        return agendaItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda Item not found with id: " + id));
    }

    @Transactional
    public AgendaItem createAgendaItem(Long meetingMinuteId, AgendaItem agendaItem) {
        MeetingMinute meetingMinute = meetingMinuteRepository.findById(meetingMinuteId)
                .orElseThrow(() -> new RuntimeException("Meeting Minute not found with id: " + meetingMinuteId));
        
        // Get the current highest display order
        List<AgendaItem> existingItems = agendaItemRepository.findByMeetingMinuteOrderByDisplayOrderAsc(meetingMinute);
        int nextOrder = existingItems.isEmpty() ? 1 : existingItems.get(existingItems.size() - 1).getDisplayOrder() + 1;
        
        agendaItem.setMeetingMinute(meetingMinute);
        agendaItem.setDisplayOrder(nextOrder);
        
        return agendaItemRepository.save(agendaItem);
    }

    @Transactional
    public AgendaItem updateAgendaItem(Long id, AgendaItem agendaItemDetails) {
        AgendaItem existingAgendaItem = getAgendaItemById(id);
        
        existingAgendaItem.setTitle(agendaItemDetails.getTitle());
        existingAgendaItem.setDescription(agendaItemDetails.getDescription());
        
        return agendaItemRepository.save(existingAgendaItem);
    }

    @Transactional
    public void deleteAgendaItem(Long id) {
        agendaItemRepository.deleteById(id);
    }
    
    @Transactional
    public void reorderAgendaItems(Long meetingMinuteId, List<Long> orderedAgendaItemIds) {
        MeetingMinute meetingMinute = meetingMinuteRepository.findById(meetingMinuteId)
                .orElseThrow(() -> new RuntimeException("Meeting Minute not found with id: " + meetingMinuteId));
        
        List<AgendaItem> agendaItems = agendaItemRepository.findByMeetingMinute(meetingMinute);
        
        // Create a map of id to agenda item for quick lookup
        java.util.Map<Long, AgendaItem> agendaItemMap = agendaItems.stream()
                .collect(Collectors.toMap(AgendaItem::getId, item -> item));
        
        // Update display order based on the provided order
        int order = 1;
        for (Long itemId : orderedAgendaItemIds) {
            AgendaItem item = agendaItemMap.get(itemId);
            if (item != null) {
                item.setDisplayOrder(order++);
                agendaItemRepository.save(item);
            }
        }
    }
}