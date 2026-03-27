package com.myfitnessjourney.dto;

import com.myfitnessjourney.entity.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteWorkoutRequest {
    private DayOfWeek dayOfWeek;
}
