package com.myfitnessjourney.dto;

import com.myfitnessjourney.entity.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private Long id;
    private String name;
    private String description;
    private DayOfWeek dayOfWeek;
    private Integer sets;
    private Integer reps;
    private BigDecimal weight;
    private String weightUnit;
    private String muscleGroup;
}
