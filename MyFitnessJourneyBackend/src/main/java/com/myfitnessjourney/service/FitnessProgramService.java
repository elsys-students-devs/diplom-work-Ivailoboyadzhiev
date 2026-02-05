package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.entity.FitnessProgram;
import com.myfitnessjourney.exception.FitnessProgramNotFoundException;
import com.myfitnessjourney.mapper.FitnessProgramMapper;
import com.myfitnessjourney.repository.FitnessProgramRepository;
import com.myfitnessjourney.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FitnessProgramService {
    private final FitnessProgramRepository fitnessProgramRepository;
    private final FitnessProgramMapper fitnessProgramMapper;

    public List<FitnessProgramDto> getAllFitnessProgramsWithExercises() {
        List<FitnessProgram> programs = fitnessProgramRepository.findAllWithExercises();
        List<FitnessProgramDto> dtos = fitnessProgramMapper.toDtoList(programs);
        Locale locale = LocaleContextHolder.getLocale();
        for (int i = 0; i < dtos.size() && i < programs.size(); i++) {
            LocalizationUtil.applyToFitnessProgram(dtos.get(i), programs.get(i), locale);
        }
        return dtos;
    }

    public Optional<FitnessProgram> getFitnessProgramById(Long id) {
        return fitnessProgramRepository.findById(id);
    }

    public FitnessProgramDto getFitnessProgramByIdWithExercises(Long id) {
        Locale locale = LocaleContextHolder.getLocale();
        return fitnessProgramRepository.findByIdWithExercises(id)
                .map(program -> {
                    FitnessProgramDto dto = fitnessProgramMapper.toDto(program);
                    LocalizationUtil.applyToFitnessProgram(dto, program, locale);
                    return dto;
                })
                .orElseThrow(() -> new FitnessProgramNotFoundException("Fitness program not found with id: " + id));
    }
    
    public boolean existsById(Long id) {
        return fitnessProgramRepository.existsById(id);
    }

    public FitnessProgram saveFitnessProgram(FitnessProgram fitnessProgram) {
        return fitnessProgramRepository.save(fitnessProgram);
    }

    public FitnessProgram getFitnessProgramByName(String name) {
        return fitnessProgramRepository.findFirstByTranslations_LocaleAndTranslations_Name("en", name)
                .or(() -> fitnessProgramRepository.findFirstByTranslations_LocaleAndTranslations_Name("bg", name))
                .orElseThrow(() -> new FitnessProgramNotFoundException("Fitness program not found: " + name));
    }
}
