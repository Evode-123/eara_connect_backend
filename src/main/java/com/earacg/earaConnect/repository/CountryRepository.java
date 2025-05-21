package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsByName(String name);
}