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
        Map<Country.CountryName, Country> countries = initializeCountries(eac);
        
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

    private Map<Country.CountryName, Country> initializeCountries(Eac eac) {
        if (countryRepository.count() == 0) {
            Arrays.stream(Country.CountryName.values()).forEach(countryName -> {
                Country country = new Country();
                country.setCountryName(countryName);
                country.setEac(eac);
                countryRepository.save(country);
                System.out.println("Added country: " + countryName);
            });
        }
        // Return a map of country names to country entities for easy lookup
        return countryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        country -> country.getCountryName(),
                        country -> country
                ));
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

    private void initializeRevenueAuthorities(Map<Country.CountryName, Country> countries) {
        if (revenueAuthorityRepository.count() == 0) {
            // Create a mapping between authority names and country names
            Map<RevenueAuthority.AuthorityName, Country.CountryName> authorityToCountry = Map.of(
                RevenueAuthority.AuthorityName.RWANDA_REVENUE_AUTHORITY, Country.CountryName.RWANDA,
                RevenueAuthority.AuthorityName.UGANDA_REVENUE_AUTHORITY, Country.CountryName.UGANDA,
                RevenueAuthority.AuthorityName.KENYA_REVENUE_AUTHORITY, Country.CountryName.KENYA,
                RevenueAuthority.AuthorityName.TANZANIA_REVENUE_AUTHORITY, Country.CountryName.TANZANIA,
                RevenueAuthority.AuthorityName.ZANZIBAR_REVENUE_AUTHORITY, Country.CountryName.ZANZIBAR,
                RevenueAuthority.AuthorityName.OFFICE_BURUNDAIS_DES_RECETTES, Country.CountryName.BURUNDI,
                RevenueAuthority.AuthorityName.SOUTH_SUDAN_REVENUE_AUTHORITY, Country.CountryName.SOUTH_SUDAN
            );

            authorityToCountry.forEach((authorityName, countryName) -> {
                RevenueAuthority authority = new RevenueAuthority();
                authority.setAuthorityName(authorityName);
                authority.setCountry(countries.get(countryName));
                revenueAuthorityRepository.save(authority);
                System.out.println("Added revenue authority: " + authorityName + " for country: " + countryName);
            });
        }
    }
}