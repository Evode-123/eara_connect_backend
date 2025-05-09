package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.model.RevenueAuthority;
import com.earacg.earaConnect.service.RevenueAuthorityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/revenue-authorities")
public class RevenueAuthorityController {
    private final RevenueAuthorityService revenueAuthorityService;

    public RevenueAuthorityController(RevenueAuthorityService revenueAuthorityService) {
        this.revenueAuthorityService = revenueAuthorityService;
    }

    @GetMapping("/get-all-revenue-authority")
    public ResponseEntity<List<RevenueAuthority>> getAllRevenueAuthorities() {
        return ResponseEntity.ok(revenueAuthorityService.getAllRevenueAuthorities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RevenueAuthority> getRevenueAuthorityById(@PathVariable Long id) {
        return ResponseEntity.ok(revenueAuthorityService.getRevenueAuthorityById(id));
    }

    @PostMapping("/country/{countryId}")
    public ResponseEntity<RevenueAuthority> createRevenueAuthority(
            @PathVariable Long countryId,
            @RequestBody RevenueAuthority revenueAuthority) {
        return ResponseEntity.ok(revenueAuthorityService.createRevenueAuthority(revenueAuthority, countryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RevenueAuthority> updateRevenueAuthority(
            @PathVariable Long id,
            @RequestBody RevenueAuthority revenueAuthorityDetails) {
        return ResponseEntity.ok(revenueAuthorityService.updateRevenueAuthority(id, revenueAuthorityDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRevenueAuthority(@PathVariable Long id) {
        revenueAuthorityService.deleteRevenueAuthority(id);
        return ResponseEntity.noContent().build();
    }
}