package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.LogMealRequest;
import com.myfitnessjourney.dto.NutritionSummaryDto;
import com.myfitnessjourney.entity.Meal;
import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.entity.UserLoggedMeal;
import com.myfitnessjourney.repository.UserRepository;
import com.myfitnessjourney.service.UserLoggedMealService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@AllArgsConstructor
public class MealLogController {

    private final UserLoggedMealService userLoggedMealService;
    private final UserRepository userRepository;

    @PostMapping("/log")
    public ResponseEntity<?> logMeal(@RequestBody LogMealRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserLoggedMeal loggedMeal = userLoggedMealService.logMeal(
                user.getId(),
                request.getMealId(),
                request.getDate() != null ? request.getDate() : LocalDate.now()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/today/summary")
    public ResponseEntity<NutritionSummaryDto> getTodayNutritionSummary() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserLoggedMeal> todayMeals = userLoggedMealService.getTodayMeals(user.getId());

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

        NutritionSummaryDto summary = new NutritionSummaryDto(
                totalCalories,
                totalProtein,
                totalCarbs,
                totalFat
        );

        return ResponseEntity.ok(summary);
    }
}

