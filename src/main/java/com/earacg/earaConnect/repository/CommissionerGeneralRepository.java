package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.CommissionerGeneral;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionerGeneralRepository extends JpaRepository<CommissionerGeneral, Long> {
    Optional<CommissionerGeneral> findByEmail(String email);
}