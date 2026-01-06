package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.entity.Diet;
import com.myfitnessjourney.mapper.DietMapper;
import com.myfitnessjourney.repository.DietRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DietService {
    private final DietRepository dietRepository;
    private final DietMapper dietMapper;

    public List<DietDto> getAllDietsWithMeals() {
        List<Diet> diets = dietRepository.findAllWithMeals();
        return dietMapper.toDtoList(diets);
    }

    public Optional<Diet> getDietById(Long id) {
        return dietRepository.findById(id);
    }

    public Optional<DietDto> getDietByIdWithMeals(Long id) {
        return dietRepository.findById(id)
                .map(dietMapper::toDto);
    }

    public Diet saveDiet(Diet diet) {
        return dietRepository.save(diet);
    }

    public Optional<Diet> getDietByName(String name) {
        return dietRepository.findByName(name);
    }
}

