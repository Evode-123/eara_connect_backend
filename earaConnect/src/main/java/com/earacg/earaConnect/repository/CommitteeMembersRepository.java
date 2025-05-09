package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.CommitteeMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeMembersRepository extends JpaRepository<CommitteeMembers, Long> {
}