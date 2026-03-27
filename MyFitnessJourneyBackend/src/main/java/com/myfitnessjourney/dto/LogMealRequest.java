package com.myfitnessjourney.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogMealRequest {
    private Long mealId;
    private LocalDate date; // Optional, defaults to today
}

