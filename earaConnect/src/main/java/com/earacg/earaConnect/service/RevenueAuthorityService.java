package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.Country;
import com.earacg.earaConnect.model.RevenueAuthority;
import com.earacg.earaConnect.repository.CountryRepository;
import com.earacg.earaConnect.repository.RevenueAuthorityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueAuthorityService {
    private final RevenueAuthorityRepository revenueAuthorityRepository;
    private final CountryRepository countryRepository;

    public List<RevenueAuthority> getAllRevenueAuthorities() {
        return revenueAuthorityRepository.findAll();
    }

    public RevenueAuthority getRevenueAuthorityById(Long id) {
        return revenueAuthorityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Revenue Authority not found with id: " + id));
    }

    @Transactional
    public RevenueAuthority createRevenueAuthority(RevenueAuthority revenueAuthority, Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
        revenueAuthority.setCountry(country);
        return revenueAuthorityRepository.save(revenueAuthority);
    }

    @Transactional
    public RevenueAuthority updateRevenueAuthority(Long id, RevenueAuthority revenueAuthorityDetails) {
        RevenueAuthority revenueAuthority = getRevenueAuthorityById(id);
        revenueAuthority.setAuthorityName(revenueAuthorityDetails.getAuthorityName());
        return revenueAuthorityRepository.save(revenueAuthority);
    }

    @Transactional
    public void deleteRevenueAuthority(Long id) {
        revenueAuthorityRepository.deleteById(id);
    }
}