package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.CreateMealRequest;
import com.myfitnessjourney.dto.MealDto;
import com.myfitnessjourney.entity.Diet;
import com.myfitnessjourney.entity.Meal;
import com.myfitnessjourney.entity.MealTranslation;
import com.myfitnessjourney.exception.DietNotFoundException;
import com.myfitnessjourney.mapper.MealMapper;
import com.myfitnessjourney.repository.MealRepository;
import com.myfitnessjourney.util.DietConstants;
import com.myfitnessjourney.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final DietService dietService;
    private final MealMapper mealMapper;

    public List<Meal> getMealsByDietId(Long dietId) {
        return mealRepository.findByDietId(dietId);
    }

    public List<MealDto> getLocalizedMealsByDietId(Long dietId, Locale locale) {
        List<Meal> meals = mealRepository.findByDietId(dietId);
        List<MealDto> mealDtos = mealMapper.toDtoList(meals);
        for (int i = 0; i < mealDtos.size() && i < meals.size(); i++) {
            LocalizationUtil.applyToMeal(mealDtos.get(i), meals.get(i), locale);
        }
        return mealDtos;
    }

    public MealDto toLocalizedMealDto(Meal meal, Locale locale) {
        MealDto dto = mealMapper.toDto(meal);
        LocalizationUtil.applyToMeal(dto, meal, locale);
        return dto;
    }

    public Meal saveMeal(Meal meal) {
        return mealRepository.save(meal);
    }

    @Transactional
    public Meal createMeal(CreateMealRequest request) {
        Diet userFavoritesDiet = dietService.getDietByName(DietConstants.USER_FAVORITES_DIET_NAME)
                .orElseThrow(() -> new DietNotFoundException("Diet not found: " + DietConstants.USER_FAVORITES_DIET_NAME));

        Meal meal = new Meal();
        meal.setCalories(request.getCalories());
        meal.setProtein(request.getProtein());
        meal.setCarbs(request.getCarbs());
        meal.setFat(request.getFat());
        meal.setFiber(request.getFiber());
        meal.setSugar(request.getSugar());
        meal.setDiet(userFavoritesDiet);

        MealTranslation translation = new MealTranslation();
        translation.setMeal(meal);
        translation.setLocale("bg");
        translation.setName(request.getName());
        translation.setDescription(request.getDescription());
        meal.setTranslations(new ArrayList<>(List.of(translation)));

        return mealRepository.save(meal);
    }
}

