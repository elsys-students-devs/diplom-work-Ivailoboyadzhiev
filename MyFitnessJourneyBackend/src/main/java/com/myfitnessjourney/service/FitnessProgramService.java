package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.entity.FitnessProgram;
import com.myfitnessjourney.exception.FitnessProgramNotFoundException;
import com.myfitnessjourney.mapper.FitnessProgramMapper;
import com.myfitnessjourney.repository.FitnessProgramRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FitnessProgramService {
    private final FitnessProgramRepository fitnessProgramRepository;
    private final FitnessProgramMapper fitnessProgramMapper;

    public List<FitnessProgramDto> getAllFitnessProgramsWithExercises() {
        List<FitnessProgram> programs = fitnessProgramRepository.findAllWithExercises();
        return fitnessProgramMapper.toDtoList(programs);
    }

    public Optional<FitnessProgram> getFitnessProgramById(Long id) {
        return fitnessProgramRepository.findById(id);
    }

    public FitnessProgramDto getFitnessProgramByIdWithExercises(Long id) {
        return fitnessProgramRepository.findByIdWithExercises(id)
                .map(fitnessProgramMapper::toDto)
                .orElseThrow(() -> new FitnessProgramNotFoundException("Fitness program not found with id: " + id));
    }
    
    public boolean existsById(Long id) {
        return fitnessProgramRepository.existsById(id);
    }

    public FitnessProgram saveFitnessProgram(FitnessProgram fitnessProgram) {
        return fitnessProgramRepository.save(fitnessProgram);
    }

    public FitnessProgram getFitnessProgramByName(String name) {
        return fitnessProgramRepository.findByName(name)
                .orElseThrow(() -> new FitnessProgramNotFoundException("Fitness program not found: " + name));
    }
}
