package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.LogMealRequest;
import com.myfitnessjourney.dto.NutritionSummaryDto;
import com.myfitnessjourney.service.UserLoggedMealService;
import com.myfitnessjourney.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@AllArgsConstructor
public class MealLogController {

    private final UserLoggedMealService userLoggedMealService;
    private final UserService userService;

    @PostMapping("/log")
    public ResponseEntity<?> logMeal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody LogMealRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();

        userLoggedMealService.logMeal(
                userId,
                request.getMealId(),
                request.getDate() != null ? request.getDate() : LocalDate.now()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/today/summary")
    public ResponseEntity<NutritionSummaryDto> getTodayNutritionSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        Long userId = userService.getUserByEmail(email).getId();

        NutritionSummaryDto summary = userLoggedMealService.getTodayNutritionSummary(userId);

        return ResponseEntity.ok(summary);
    }
}

