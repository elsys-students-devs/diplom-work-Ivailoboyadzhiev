package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.NutritionSummaryDto;
import com.myfitnessjourney.entity.Meal;
import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.entity.UserLoggedMeal;
import com.myfitnessjourney.exception.MealNotFoundException;
import com.myfitnessjourney.exception.UserNotFoundException;
import com.myfitnessjourney.repository.MealRepository;
import com.myfitnessjourney.repository.UserLoggedMealRepository;
import com.myfitnessjourney.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class UserLoggedMealService {
    private final UserLoggedMealRepository userLoggedMealRepository;
    private final UserRepository userRepository;
    private final MealRepository mealRepository;

    @Transactional
    public UserLoggedMeal logMeal(Long userId, Long mealId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException(mealId));

        UserLoggedMeal loggedMeal = UserLoggedMeal.builder()
                .user(user)
                .meal(meal)
                .loggedDate(date != null ? date : LocalDate.now())
                .build();

        return userLoggedMealRepository.save(loggedMeal);
    }

    public List<UserLoggedMeal> getTodayMeals(Long userId) {
        return userLoggedMealRepository.findMealsByUserIdAndDate(userId, LocalDate.now());
    }

    public List<UserLoggedMeal> getMealsByDate(Long userId, LocalDate date) {
        return userLoggedMealRepository.findByUserIdAndLoggedDate(userId, date);
    }

    public NutritionSummaryDto getTodayNutritionSummary(Long userId) {
        List<UserLoggedMeal> todayMeals = getTodayMeals(userId);

        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarbs = 0.0;
        double totalFat = 0.0;

        for (UserLoggedMeal loggedMeal : todayMeals) {
            Meal meal = loggedMeal.getMeal();
            if (meal != null) {
                totalCalories += meal.getCalories() != null ? meal.getCalories() : 0.0;
                totalProtein += meal.getProtein() != null ? meal.getProtein() : 0.0;
                totalCarbs += meal.getCarbs() != null ? meal.getCarbs() : 0.0;
                totalFat += meal.getFat() != null ? meal.getFat() : 0.0;
            }
        }

        return new NutritionSummaryDto(totalCalories, totalProtein, totalCarbs, totalFat);
    }
}

