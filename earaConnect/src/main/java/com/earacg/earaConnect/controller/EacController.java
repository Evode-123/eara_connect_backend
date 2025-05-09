package com.earacg.earaConnect.controller;

import com.earacg.earaConnect.model.Eac;
import com.earacg.earaConnect.service.EacService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/eacs")
public class EacController {
    private final EacService eacService;

    public EacController(EacService eacService) {
        this.eacService = eacService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Eac>> getAllEacs() {
        return ResponseEntity.ok(eacService.getAllEacs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Eac> getEacById(@PathVariable Long id) {
        return ResponseEntity.ok(eacService.getEacById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Eac> createEac(@RequestBody Eac eac) {
        return ResponseEntity.ok(eacService.createEac(eac));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Eac> updateEac(@PathVariable Long id, @RequestBody Eac eacDetails) {
        return ResponseEntity.ok(eacService.updateEac(id, eacDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEac(@PathVariable Long id) {
        eacService.deleteEac(id);
        return ResponseEntity.noContent().build();
    }
}