package com.myfitnessjourney.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietDto {
    private Long id;
    private String name;
    private String description;
    private String benefits;
    private List<MealDto> meals;
}

