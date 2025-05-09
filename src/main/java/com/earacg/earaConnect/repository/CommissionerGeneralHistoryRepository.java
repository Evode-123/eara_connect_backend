package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.CommissionerGeneralHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionerGeneralHistoryRepository extends JpaRepository<CommissionerGeneralHistory, Long> {
}