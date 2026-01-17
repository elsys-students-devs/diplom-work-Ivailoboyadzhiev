package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.exception.FitnessProgramNotFoundException;
import com.myfitnessjourney.service.ExerciseService;
import com.myfitnessjourney.service.FitnessProgramService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fitness-programs")
@AllArgsConstructor
public class FitnessProgramController {

    private final FitnessProgramService fitnessProgramService;
    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<List<FitnessProgramDto>> getAllFitnessPrograms() {
        List<FitnessProgramDto> programDtos = fitnessProgramService.getAllFitnessProgramsWithExercises();
        return ResponseEntity.ok(programDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FitnessProgramDto> getFitnessProgramById(@PathVariable Long id) {
        FitnessProgramDto program = fitnessProgramService.getFitnessProgramByIdWithExercises(id);
        return ResponseEntity.ok(program);
    }

    @GetMapping("/{id}/exercises")
    public ResponseEntity<List<ExerciseDto>> getExercisesByFitnessProgramId(@PathVariable Long id) {
        if (!fitnessProgramService.existsById(id)) {
            throw new FitnessProgramNotFoundException("Fitness program not found with id: " + id);
        }
        List<ExerciseDto> exerciseDtos = exerciseService.getExercisesByFitnessProgramId(id);
        return ResponseEntity.ok(exerciseDtos);
    }
}
