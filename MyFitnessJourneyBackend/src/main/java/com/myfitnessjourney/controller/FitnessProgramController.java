package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.entity.Exercise;
import com.myfitnessjourney.mapper.ExerciseMapper;
import com.myfitnessjourney.service.ExerciseService;
import com.myfitnessjourney.service.FitnessProgramService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fitness-programs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@AllArgsConstructor
public class FitnessProgramController {

    private final FitnessProgramService fitnessProgramService;
    private final ExerciseService exerciseService;
    private final ExerciseMapper exerciseMapper;

    @GetMapping
    public ResponseEntity<List<FitnessProgramDto>> getAllFitnessPrograms() {
        List<FitnessProgramDto> programDtos = fitnessProgramService.getAllFitnessProgramsWithExercises();
        return ResponseEntity.ok(programDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FitnessProgramDto> getFitnessProgramById(@PathVariable Long id) {
        return fitnessProgramService.getFitnessProgramByIdWithExercises(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/exercises")
    public ResponseEntity<List<ExerciseDto>> getExercisesByFitnessProgramId(@PathVariable Long id) {
        List<Exercise> exercises = exerciseService.getExercisesByFitnessProgramId(id);
        List<ExerciseDto> exerciseDtos = exerciseMapper.toDtoList(exercises);
        return ResponseEntity.ok(exerciseDtos);
    }
}
