package com.myfitnessjourney.service;

import com.myfitnessjourney.entity.Exercise;
import com.myfitnessjourney.repository.ExerciseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public List<Exercise> getExercisesByFitnessProgramId(Long fitnessProgramId) {
        return exerciseRepository.findByFitnessProgramId(fitnessProgramId);
    }

    public List<Exercise> getExercisesByFitnessProgramIdAndDay(Long fitnessProgramId, String dayOfWeek) {
        return exerciseRepository.findByFitnessProgramIdAndDayOfWeek(fitnessProgramId, dayOfWeek);
    }

    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }
}
