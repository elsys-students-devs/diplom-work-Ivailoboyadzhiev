package com.myfitnessjourney.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMealRequest {
    @NotBlank(message = "Meal name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Calories are required")
    @PositiveOrZero(message = "Calories must be positive or zero")
    private Double calories;
    
    @NotNull(message = "Protein is required")
    @PositiveOrZero(message = "Protein must be positive or zero")
    private Double protein;
    
    @NotNull(message = "Carbs are required")
    @PositiveOrZero(message = "Carbs must be positive or zero")
    private Double carbs;
    
    @NotNull(message = "Fat is required")
    @PositiveOrZero(message = "Fat must be positive or zero")
    private Double fat;
    
    @PositiveOrZero(message = "Fiber must be positive or zero")
    private Double fiber;
    
    @PositiveOrZero(message = "Sugar must be positive or zero")
    private Double sugar;
}

