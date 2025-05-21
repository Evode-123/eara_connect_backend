package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.Country;
import com.earacg.earaConnect.model.Eac;
import com.earacg.earaConnect.model.RevenueAuthority;
import com.earacg.earaConnect.repository.CountryRepository;
import com.earacg.earaConnect.repository.EacRepository;
import com.earacg.earaConnect.repository.RevenueAuthorityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private final EacRepository eacRepository;
    private final RevenueAuthorityRepository revenueAuthorityRepository;

    public void addCustomCountry(String name, String isoCode, Long eacId) {
        if (countryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Country already exists");
        }
    
        var eac = eacRepository.findById(eacId)
                .orElseThrow(() -> new IllegalArgumentException("EAC not found"));
    
        Country country = new Country();
        country.setName(name);
        country.setIsoCode(isoCode); // Set the ISO code
        country.setEac(eac);
    
        countryRepository.save(country);
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public Country getCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
    }

    @Transactional
    public Country createCountry(Country country, Long eacId) {
        Eac eac = eacRepository.findById(eacId)
                .orElseThrow(() -> new RuntimeException("EAC not found with id: " + eacId));
        country.setEac(eac);
        return countryRepository.save(country);
    }

    @Transactional
    public Country updateCountry(Long id, Country countryDetails) {
        Country country = getCountryById(id);
        country.setName(countryDetails.getName());
        return countryRepository.save(country);
    }

    @Transactional
    public void deleteCountry(Long id) {
        countryRepository.deleteById(id);
    }

    public List<RevenueAuthority> getRevenueAuthoritiesByCountry(Long countryId) {
    Country country = countryRepository.findById(countryId)
            .orElseThrow(() -> new RuntimeException("Country not found"));
    return revenueAuthorityRepository.findByCountry(country);
}

}