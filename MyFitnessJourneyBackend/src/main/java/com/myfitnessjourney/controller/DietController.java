package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.CreateMealRequest;
import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.dto.MealDto;
import com.myfitnessjourney.entity.Diet;
import com.myfitnessjourney.entity.Meal;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diets")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@AllArgsConstructor
public class DietController {

    private final DietService dietService;
    private final MealService mealService;

    @GetMapping
    public ResponseEntity<List<DietDto>> getAllDiets() {
        List<Diet> diets = dietService.getAllDiets();
        List<DietDto> dietDtos = diets.stream()
                .map(diet -> convertToDto(diet, mealService.getMealsByDietId(diet.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dietDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietDto> getDietById(@PathVariable Long id) {
        return dietService.getDietById(id)
                .map(diet -> {
                    List<Meal> meals = mealService.getMealsByDietId(diet.getId());
                    return ResponseEntity.ok(convertToDto(diet, meals));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/meals")
    public ResponseEntity<List<MealDto>> getMealsByDietId(@PathVariable Long id) {
        List<Meal> meals = mealService.getMealsByDietId(id);
        List<MealDto> mealDtos = meals.stream()
                .map(this::convertMealToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mealDtos);
    }

    @PostMapping("/meals/create")
    public ResponseEntity<MealDto> createMeal(@RequestBody CreateMealRequest request) {
        // Get "User Favorites" diet
        Diet userFavoritesDiet = dietService.getDietByName("User Favorites");
        
        // Create new meal
        Meal meal = new Meal();
        meal.setName(request.getName());
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein(request.getProtein());
        meal.setCarbs(request.getCarbs());
        meal.setFat(request.getFat());
        meal.setFiber(request.getFiber());
        meal.setSugar(request.getSugar());
        meal.setDiet(userFavoritesDiet);
        
        Meal savedMeal = mealService.saveMeal(meal);
        
        return ResponseEntity.ok(convertMealToDto(savedMeal));
    }

    private DietDto convertToDto(Diet diet, List<Meal> meals) {
        List<MealDto> mealDtos = meals != null ?
                meals.stream()
                        .map(this::convertMealToDto)
                        .collect(Collectors.toList()) :
                List.of();
        
        return new DietDto(
                diet.getId(),
                diet.getName(),
                diet.getDescription(),
                diet.getBenefits(),
                mealDtos
        );
    }

    private MealDto convertMealToDto(Meal meal) {
        return new MealDto(
                meal.getId(),
                meal.getName(),
                meal.getDescription(),
                meal.getCalories(),
                meal.getProtein(),
                meal.getCarbs(),
                meal.getFat(),
                meal.getFiber(),
                meal.getSugar()
        );
    }
}

