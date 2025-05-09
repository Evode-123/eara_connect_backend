package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.CommissionerGeneral;
import com.earacg.earaConnect.model.CommissionerGeneralHistory;
import com.earacg.earaConnect.model.Country;
import com.earacg.earaConnect.repository.CommissionerGeneralHistoryRepository;
import com.earacg.earaConnect.repository.CommissionerGeneralRepository;
import com.earacg.earaConnect.repository.CountryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionerGeneralService {
    private final CommissionerGeneralRepository commissionerGeneralRepository;
    private final CommissionerGeneralHistoryRepository historyRepository;
    private final CountryRepository countryRepository;

    public List<CommissionerGeneral> getAllCommissioners() {
        return commissionerGeneralRepository.findAll();
    }

    public CommissionerGeneral getCommissionerById(Long id) {
        return commissionerGeneralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commissioner General not found with id: " + id));
    }

    @Transactional
    public CommissionerGeneral createCommissioner(CommissionerGeneral commissioner, Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
        commissioner.setCountry(country);
        return commissionerGeneralRepository.save(commissioner);
    }

    @Transactional
    public CommissionerGeneral updateCommissioner(Long id, CommissionerGeneral commissionerDetails) {
        CommissionerGeneral existing = getCommissionerById(id);
        
        // Save to history before updating
        saveToHistory(existing);
        
        existing.setCgName(commissionerDetails.getCgName());
        existing.setCgPhone(commissionerDetails.getCgPhone());
        existing.setCgEmail(commissionerDetails.getCgEmail());
        return commissionerGeneralRepository.save(existing);
    }

    private void saveToHistory(CommissionerGeneral commissioner) {
        CommissionerGeneralHistory history = new CommissionerGeneralHistory();
        history.setCgName(commissioner.getCgName());
        history.setCgPhone(commissioner.getCgPhone());
        history.setCgEmail(commissioner.getCgEmail());
        history.setCountry(commissioner.getCountry());
        history.setUpdatedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Transactional
    public void deleteCommissioner(Long id) {
        commissionerGeneralRepository.deleteById(id);
    }
}