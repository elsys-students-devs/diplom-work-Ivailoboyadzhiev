package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.entity.DayOfWeek;
import com.myfitnessjourney.entity.Exercise;
import com.myfitnessjourney.mapper.ExerciseMapper;
import com.myfitnessjourney.repository.ExerciseRepository;
import com.myfitnessjourney.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    public List<ExerciseDto> getExercisesByFitnessProgramId(Long fitnessProgramId) {
        List<Exercise> exercises = exerciseRepository.findByFitnessProgramId(fitnessProgramId);
        List<ExerciseDto> dtos = exerciseMapper.toDtoList(exercises);
        Locale locale = LocaleContextHolder.getLocale();
        for (int i = 0; i < dtos.size() && i < exercises.size(); i++) {
            LocalizationUtil.applyToExercise(dtos.get(i), exercises.get(i), locale);
        }
        return dtos;
    }

    public List<Exercise> getExercisesByFitnessProgramIdAndDay(Long fitnessProgramId, DayOfWeek dayOfWeek) {
        return exerciseRepository.findByFitnessProgramIdAndDayOfWeek(fitnessProgramId, dayOfWeek);
    }

    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }
}
