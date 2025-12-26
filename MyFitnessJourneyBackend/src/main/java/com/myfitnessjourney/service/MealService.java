package com.myfitnessjourney.service;

import com.myfitnessjourney.entity.Meal;
import com.myfitnessjourney.repository.MealRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MealService {
    private final MealRepository mealRepository;

    public List<Meal> getMealsByDietId(Long dietId) {
        return mealRepository.findByDietId(dietId);
    }

    public Meal saveMeal(Meal meal) {
        return mealRepository.save(meal);
    }
}

