package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.service.CommissionerGeneralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/commissioners")
public class CommissionerGeneralController {
    private final CommissionerGeneralService commissionerGeneralService;

    public CommissionerGeneralController(CommissionerGeneralService commissionerGeneralService) {
        this.commissionerGeneralService = commissionerGeneralService;
    }

    @GetMapping
    public ResponseEntity<List<CommissionerGeneral>> getAllCommissioners() {
        return ResponseEntity.ok(commissionerGeneralService.getAllCommissioners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommissionerGeneral> getCommissionerById(@PathVariable Long id) {
        return ResponseEntity.ok(commissionerGeneralService.getCommissionerById(id));
    }

    @PostMapping("/country/{countryId}")
    public ResponseEntity<CommissionerGeneral> createCommissioner(
            @PathVariable Long countryId,
            @RequestBody CommissionerGeneral commissioner) {
        return ResponseEntity.ok(commissionerGeneralService.createCommissioner(commissioner, countryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommissionerGeneral> updateCommissioner(
            @PathVariable Long id,
            @RequestBody CommissionerGeneral commissionerDetails) {
        return ResponseEntity.ok(commissionerGeneralService.updateCommissioner(id, commissionerDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommissioner(@PathVariable Long id) {
        commissionerGeneralService.deleteCommissioner(id);
        return ResponseEntity.noContent().build();
    }
}