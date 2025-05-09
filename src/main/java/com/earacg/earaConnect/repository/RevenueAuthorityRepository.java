package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.Country;
import com.earacg.earaConnect.model.RevenueAuthority;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevenueAuthorityRepository extends JpaRepository<RevenueAuthority, Long> {

    List<RevenueAuthority> findByCountry(Country country);
}