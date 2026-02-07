package com.myfitnessjourney.dto;

import com.myfitnessjourney.entity.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompletedDaysDto {
    private Set<DayOfWeek> completedDays;
}
