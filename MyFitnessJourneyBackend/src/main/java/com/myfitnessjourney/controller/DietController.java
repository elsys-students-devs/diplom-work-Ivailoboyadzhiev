package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.CreateMealRequest;
import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.dto.MealDto;
import com.myfitnessjourney.entity.Meal;
import com.myfitnessjourney.mapper.MealMapper;
import com.myfitnessjourney.service.DietService;
import com.myfitnessjourney.service.MealService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/diets")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@AllArgsConstructor
public class DietController {

    private final DietService dietService;
    private final MealService mealService;
    private final MealMapper mealMapper;

    @GetMapping
    public ResponseEntity<List<DietDto>> getAllDiets() {
        List<DietDto> dietDtos = dietService.getAllDietsWithMeals();
        return ResponseEntity.ok(dietDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietDto> getDietById(@PathVariable Long id) {
        return dietService.getDietByIdWithMeals(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/meals")
    public ResponseEntity<List<MealDto>> getMealsByDietId(@PathVariable Long id) {
        List<Meal> meals = mealService.getMealsByDietId(id);
        List<MealDto> mealDtos = mealMapper.toDtoList(meals);
        return ResponseEntity.ok(mealDtos);
    }

    @PostMapping("/meals")
    public ResponseEntity<MealDto> createMeal(@Valid @RequestBody CreateMealRequest request) { 
        Meal savedMeal = mealService.createMeal(request);
        MealDto mealDto = mealMapper.toDto(savedMeal);
        return ResponseEntity.ok(mealDto);
    }
}

