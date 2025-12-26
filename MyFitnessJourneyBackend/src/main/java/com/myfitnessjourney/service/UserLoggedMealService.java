package com.myfitnessjourney.service;

import com.myfitnessjourney.entity.Meal;
import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.entity.UserLoggedMeal;
import com.myfitnessjourney.repository.MealRepository;
import com.myfitnessjourney.repository.UserLoggedMealRepository;
import com.myfitnessjourney.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserLoggedMealService {
    private final UserLoggedMealRepository userLoggedMealRepository;
    private final UserRepository userRepository;
    private final MealRepository mealRepository;

    @Transactional
    public UserLoggedMeal logMeal(Long userId, Long mealId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        UserLoggedMeal loggedMeal = new UserLoggedMeal();
        loggedMeal.setUser(user);
        loggedMeal.setMeal(meal);
        loggedMeal.setLoggedDate(date != null ? date : LocalDate.now());
        loggedMeal.setCreatedAt(LocalDateTime.now());

        return userLoggedMealRepository.save(loggedMeal);
    }

    public List<UserLoggedMeal> getTodayMeals(Long userId) {
        return userLoggedMealRepository.findTodayMealsByUserId(userId, LocalDate.now());
    }

    public List<UserLoggedMeal> getMealsByDate(Long userId, LocalDate date) {
        return userLoggedMealRepository.findByUserIdAndLoggedDate(userId, date);
    }
}

