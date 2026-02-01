package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.dto.FitnessProgramDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroqContextBuilder {

    @Cacheable(value = "groqContext", key = "#fitnessPrograms.![id].toString() + '-' + #diets.![id].toString()")
    public String buildContext(List<FitnessProgramDto> fitnessPrograms, List<DietDto> diets) {
        StringBuilder context = new StringBuilder();

        context.append("Достъпни фитнес програми:\n");
        for (FitnessProgramDto program : fitnessPrograms) {
            context.append(String.format("- %s (ID: %d): %s\n",
                program.getName(), program.getId(), program.getDescription()));
            if (program.getBenefits() != null) {
                context.append(String.format("  Предимства: %s\n", program.getBenefits()));
            }
            if (program.getExercises() != null && !program.getExercises().isEmpty()) {
                context.append("  Упражнения:\n");
                program.getExercises().forEach(exercise -> {
                    context.append(String.format("    - %s (%s)", exercise.getName(), exercise.getDayOfWeek()));
                    if (exercise.getSets() != null && exercise.getReps() != null) {
                        context.append(String.format(": %d серии x %d повторения",
                            exercise.getSets(), exercise.getReps()));
                    }
                    context.append("\n");
                });
            }
        }

        context.append("\nДостъпни диети:\n");
        for (DietDto diet : diets) {
            context.append(String.format("- %s (ID: %d): %s\n",
                diet.getName(), diet.getId(), diet.getDescription()));
            if (diet.getMeals() != null && !diet.getMeals().isEmpty()) {
                context.append("  Ястия:\n");
                diet.getMeals().forEach(meal ->
                    context.append(String.format("    - %s: %s\n", meal.getName(), meal.getDescription())));
            }
        }

        return context.toString();
    }
}
