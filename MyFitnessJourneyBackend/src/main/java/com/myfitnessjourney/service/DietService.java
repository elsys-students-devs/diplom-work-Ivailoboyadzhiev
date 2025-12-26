package com.myfitnessjourney.service;

import com.myfitnessjourney.entity.Diet;
import com.myfitnessjourney.repository.DietRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DietService {
    private final DietRepository dietRepository;

    public List<Diet> getAllDiets() {
        return dietRepository.findAll();
    }

    public Optional<Diet> getDietById(Long id) {
        return dietRepository.findById(id);
    }

    public Diet saveDiet(Diet diet) {
        return dietRepository.save(diet);
    }

    public Diet getDietByName(String name) {
        return dietRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Diet not found: " + name));
    }
}

