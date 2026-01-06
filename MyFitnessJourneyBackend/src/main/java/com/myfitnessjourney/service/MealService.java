package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.CreateMealRequest;
import com.myfitnessjourney.entity.Diet;
import com.myfitnessjourney.entity.Meal;
import com.myfitnessjourney.exception.DietNotFoundException;
import com.myfitnessjourney.repository.MealRepository;
import com.myfitnessjourney.util.DietConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final DietService dietService;

    public List<Meal> getMealsByDietId(Long dietId) {
        return mealRepository.findByDietId(dietId);
    }

    public Meal saveMeal(Meal meal) {
        return mealRepository.save(meal);
    }

    @Transactional
    public Meal createMeal(CreateMealRequest request) {
        Diet userFavoritesDiet = dietService.getDietByName(DietConstants.USER_FAVORITES_DIET_NAME)
                .orElseThrow(() -> new DietNotFoundException("Diet not found: " + DietConstants.USER_FAVORITES_DIET_NAME));

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

        return mealRepository.save(meal);
    }
}

