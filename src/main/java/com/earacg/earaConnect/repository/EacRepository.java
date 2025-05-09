package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.Eac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EacRepository extends JpaRepository<Eac, Long> {
}