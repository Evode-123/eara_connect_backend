package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.CommitteeMembersHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeMembersHistoryRepository extends JpaRepository<CommitteeMembersHistory, Long> {
}