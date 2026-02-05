package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.entity.Diet;
import com.myfitnessjourney.mapper.DietMapper;
import com.myfitnessjourney.repository.DietRepository;
import com.myfitnessjourney.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DietService {
    private final DietRepository dietRepository;
    private final DietMapper dietMapper;

    public List<DietDto> getAllDietsWithMeals() {
        List<Diet> diets = dietRepository.findAllWithMeals();
        List<DietDto> dtos = dietMapper.toDtoList(diets);
        Locale locale = LocaleContextHolder.getLocale();
        for (int i = 0; i < dtos.size() && i < diets.size(); i++) {
            LocalizationUtil.applyToDiet(dtos.get(i), diets.get(i), locale);
        }
        return dtos;
    }

    public Optional<Diet> getDietById(Long id) {
        return dietRepository.findById(id);
    }

    public Optional<DietDto> getDietByIdWithMeals(Long id) {
        Locale locale = LocaleContextHolder.getLocale();
        return dietRepository.findById(id)
                .map(diet -> {
                    DietDto dto = dietMapper.toDto(diet);
                    LocalizationUtil.applyToDiet(dto, diet, locale);
                    return dto;
                });
    }

    public Diet saveDiet(Diet diet) {
        return dietRepository.save(diet);
    }

    public Optional<Diet> getDietByName(String name) {
        return dietRepository.findByName(name);
    }
}

