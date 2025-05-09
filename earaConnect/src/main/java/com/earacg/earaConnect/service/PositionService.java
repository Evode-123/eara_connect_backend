package com.earacg.earaConnect.service;

import com.earacg.earaConnect.model.Position;
import com.earacg.earaConnect.repository.PositionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Position getPositionById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + id));
    }

    @Transactional
    public Position createPosition(Position position) {
        return positionRepository.save(position);
    }

    @Transactional
    public Position updatePosition(Long id, Position positionDetails) {
        Position position = getPositionById(id);
        position.setPositionName(positionDetails.getPositionName());
        return positionRepository.save(position);
    }

    @Transactional
    public void deletePosition(Long id) {
        positionRepository.deleteById(id);
    }
}