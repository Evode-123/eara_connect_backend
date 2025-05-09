package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.Country;
import com.earacg.earaConnect.model.Position;
import com.earacg.earaConnect.model.Resolution;
import com.earacg.earaConnect.model.ResolutionAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResolutionAssignmentRepository extends JpaRepository<ResolutionAssignment, Long> {
    List<ResolutionAssignment> findByResolution(Resolution resolution);
    List<ResolutionAssignment> findByAssigneeTypeAndAssignedCountry(Resolution.AssigneeType assigneeType, Country country);
    List<ResolutionAssignment> findByAssigneeTypeAndAssignedPosition(Resolution.AssigneeType assigneeType, Position position);
    List<ResolutionAssignment> findByAssigneeTypeAndAssignedToAllCommissionersTrue(Resolution.AssigneeType assigneeType);
}