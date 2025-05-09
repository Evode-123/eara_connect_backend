package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.Eac;
import com.earacg.earaConnect.repository.EacRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EacService {
    private final EacRepository eacRepository;

    public List<Eac> getAllEacs() {
        return eacRepository.findAll();
    }

    public Eac getEacById(Long id) {
        return eacRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EAC not found with id: " + id));
    }

    @Transactional
    public Eac createEac(Eac eac) {
        return eacRepository.save(eac);
    }

    @Transactional
    public Eac updateEac(Long id, Eac eacDetails) {
        Eac eac = getEacById(id);
        eac.setEacName(eacDetails.getEacName());
        return eacRepository.save(eac);
    }

    @Transactional
    public void deleteEac(Long id) {
        eacRepository.deleteById(id);
    }
}