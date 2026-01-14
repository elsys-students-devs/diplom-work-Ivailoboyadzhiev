package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.entity.DayOfWeek;
import com.myfitnessjourney.entity.Exercise;
import com.myfitnessjourney.mapper.ExerciseMapper;
import com.myfitnessjourney.repository.ExerciseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    public List<ExerciseDto> getExercisesByFitnessProgramId(Long fitnessProgramId) {
        List<Exercise> exercises = exerciseRepository.findByFitnessProgramId(fitnessProgramId);
        return exerciseMapper.toDtoList(exercises);
    }

    public List<Exercise> getExercisesByFitnessProgramIdAndDay(Long fitnessProgramId, DayOfWeek dayOfWeek) {
        return exerciseRepository.findByFitnessProgramIdAndDayOfWeek(fitnessProgramId, dayOfWeek);
    }

    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }
}
