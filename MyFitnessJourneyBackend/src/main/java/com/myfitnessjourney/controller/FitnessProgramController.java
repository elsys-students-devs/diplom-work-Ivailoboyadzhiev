package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.dto.FitnessProgramDto;
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
        List<ExerciseDto> exerciseDtos = fitnessProgramService.getExercisesByProgramId(id);
        return ResponseEntity.ok(exerciseDtos);
    }
}
