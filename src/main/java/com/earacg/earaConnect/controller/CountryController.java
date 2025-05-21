package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.model.Country;
import com.earacg.earaConnect.model.RevenueAuthority;
import com.earacg.earaConnect.service.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.earacg.earaConnect.dto.CountryRequest;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/get-all-country")
    public ResponseEntity<List<Country>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable Long id) {
        return ResponseEntity.ok(countryService.getCountryById(id));
    }

    @PostMapping("/eac/{eacId}")
    public ResponseEntity<Country> createCountry(
            @PathVariable Long eacId,
            @RequestBody Country country) {
        return ResponseEntity.ok(countryService.createCountry(country, eacId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(
            @PathVariable Long id,
            @RequestBody Country countryDetails) {
        return ResponseEntity.ok(countryService.updateCountry(id, countryDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{countryId}/revenue-authorities")
    public ResponseEntity<List<RevenueAuthority>> getRevenueAuthoritiesByCountry(
        @PathVariable Long countryId) {
    return ResponseEntity.ok(countryService.getRevenueAuthoritiesByCountry(countryId));
    }

    @PostMapping("/add-custom")
    public ResponseEntity<String> addCustomCountry(@RequestBody CountryRequest request) {
        try {
            countryService.addCustomCountry(request.getName(), request.getIsoCode(), request.getEacId());
            return ResponseEntity.ok("Country added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}