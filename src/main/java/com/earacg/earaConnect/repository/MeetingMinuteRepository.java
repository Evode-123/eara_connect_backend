package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.MeetingMinute;
import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.model.CommitteeMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MeetingMinuteRepository extends JpaRepository<MeetingMinute, Long> {
    List<MeetingMinute> findByCreatedBy(CommitteeMembers createdBy);
    
    List<MeetingMinute> findByMeetingType(MeetingMinute.MeetingType meetingType);
    
    List<MeetingMinute> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT mm FROM MeetingMinute mm WHERE :committeeMember MEMBER OF mm.committeeParticipants")
    List<MeetingMinute> findByCommitteeMemberParticipant(@Param("committeeMember") CommitteeMembers committeeMember);
    
    @Query("SELECT mm FROM MeetingMinute mm WHERE :commissioner MEMBER OF mm.commissionerParticipants")
    List<MeetingMinute> findByCommissionerParticipant(@Param("commissioner") CommissionerGeneral commissioner);
}
