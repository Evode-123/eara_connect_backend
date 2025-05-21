package com.earacg.earaConnect.repository;

import com.earacg.earaConnect.model.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EnumInitializer implements ApplicationRunner {

    private final PositionRepository positionRepository;
    private final EacRepository eacRepository;
    private final CountryRepository countryRepository;
    private final RevenueAuthorityRepository revenueAuthorityRepository;

    public EnumInitializer(PositionRepository positionRepository,
                           EacRepository eacRepository,
                           CountryRepository countryRepository,
                           RevenueAuthorityRepository revenueAuthorityRepository) {
        this.positionRepository = positionRepository;
        this.eacRepository = eacRepository;
        this.countryRepository = countryRepository;
        this.revenueAuthorityRepository = revenueAuthorityRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Initialize EAC first
        Eac eac = initializeEac();

        // Initialize Countries
        Map<String, Country> countries = initializeCountries(eac);

        // Initialize Positions
        initializePositions();

        // Initialize Revenue Authorities
        initializeRevenueAuthorities(countries);
    }

    private Eac initializeEac() {
        if (eacRepository.count() == 0) {
            Eac eac = new Eac();
            eac.setEacName(Eac.EacName.EARACG);
            return eacRepository.save(eac);
        }
        return eacRepository.findAll().get(0);
    }

    private Map<String, Country> initializeCountries(Eac eac) {
        if (countryRepository.count() == 0) {
            Map<String, String> predefinedCountriesWithIsoCodes = Map.of(
                "Rwanda", "RW",
                "Uganda", "UG",
                "Kenya", "KE",
                "Tanzania", "TZ",
                "Zanzibar", "TZ", // Zanzibar is part of Tanzania, so it uses TZ
                "Burundi", "BI",
                "South Sudan", "SS"
            );
    
            predefinedCountriesWithIsoCodes.forEach((countryName, isoCode) -> {
                Country country = new Country();
                country.setName(countryName);
                country.setIsoCode(isoCode); // Set the ISO code
                country.setEac(eac);
                countryRepository.save(country);
                System.out.println("Added country: " + countryName + " with ISO code: " + isoCode);
            });
        }
    
        // Return a map of country names to country entities for easy lookup
        Map<String, Country> countries = countryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Country::getName,
                        country -> country
                ));
    
        // Debugging: Print the countries map
        System.out.println("Countries map: " + countries);
    
        return countries;
    }

    private void initializePositions() {
        if (positionRepository.count() == 0) {
            Arrays.stream(Position.PositionName.values()).forEach(positionName -> {
                Position position = new Position();
                position.setPositionName(positionName);
                positionRepository.save(position);
                System.out.println("Added position: " + positionName);
            });
        }
    }

    private void initializeRevenueAuthorities(Map<String, Country> countries) {
        if (revenueAuthorityRepository.count() == 0) {
            // Create a mapping between authority names and country names
            Map<String, String> authorityToCountry = Map.of(
                "Rwanda Revenue Authority", "Rwanda",
                "Uganda Revenue Authority", "Uganda",
                "Kenya Revenue Authority", "Kenya",
                "Tanzania Revenue Authority", "Tanzania",
                "Zanzibar Revenue Authority", "Zanzibar",
                "Office Burundais des Recettes", "Burundi",
                "South Sudan Revenue Authority", "South Sudan"
            );
    
            authorityToCountry.forEach((authorityName, countryName) -> {
                Country country = countries.get(countryName); // Retrieve the country by name
                if (country == null) {
                    throw new RuntimeException("Country not found for name: " + countryName);
                }
    
                RevenueAuthority authority = new RevenueAuthority();
                authority.setAuthorityName(authorityName);
                authority.setCountry(country); // Set the country
                revenueAuthorityRepository.save(authority);
                System.out.println("Added revenue authority: " + authorityName + " for country: " + countryName);
            });
        }
    }
}