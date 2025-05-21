package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.CommitteeMembers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeMembersRepository extends JpaRepository<CommitteeMembers, Long> {
    Optional<CommitteeMembers> findByEmail(String email);

    @Query("SELECT new com.earacg.earaConnect.model.CommitteeMembers(c.id, c.name, c.phone, c.email, " +
           "c.currentPositionInYourRRA, c.positionInERA, c.appointedDate, c.appointedLetterName, " +
           "c.appointedLetterType, c.country, c.revenueAuthority, c.memberType, c.password, " +
           "c.firstLogin, c.gender, c.currentJobPosition, c.department, c.employmentDate) " +
           "FROM CommitteeMembers c WHERE c.email = :email")
    Optional<CommitteeMembers> findByEmailExcludingLob(@Param("email") String email);
    List<CommitteeMembers> findByCountryId(Long countryId);
}