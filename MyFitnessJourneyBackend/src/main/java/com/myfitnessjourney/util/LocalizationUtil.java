package com.myfitnessjourney.util;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.dto.ExerciseDto;
import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.dto.MealDto;
import com.myfitnessjourney.entity.Diet;
import com.myfitnessjourney.entity.DietTranslation;
import com.myfitnessjourney.entity.Exercise;
import com.myfitnessjourney.entity.ExerciseTranslation;
import com.myfitnessjourney.entity.FitnessProgram;
import com.myfitnessjourney.entity.FitnessProgramTranslation;
import com.myfitnessjourney.entity.Meal;
import com.myfitnessjourney.entity.MealTranslation;

import java.util.List;
import java.util.Locale;


//Applies locale (en/bg) to DTOs using translation tables.

public final class LocalizationUtil {

    private static final String EN = "en";

    private LocalizationUtil() {
    }

    public static boolean useEnglish(Locale locale) {
        return locale != null && EN.equalsIgnoreCase(locale.getLanguage());
    }

    public static void applyToDiet(DietDto dto, Diet entity, Locale locale) {
        if (dto == null || entity == null) return;
        String localeCode = useEnglish(locale) ? EN : "bg";
        DietTranslation t = entity.getTranslation(localeCode).orElse(entity.getDefaultTranslation());
        if (t != null) {
            dto.setName(t.getName());
            dto.setDescription(t.getDescription());
            dto.setBenefits(t.getBenefits());
        }
        if (dto.getMeals() != null && entity.getMeals() != null) {
            List<Meal> meals = entity.getMeals();
            List<MealDto> mealDtos = dto.getMeals();
            for (int i = 0; i < Math.min(meals.size(), mealDtos.size()); i++) {
                applyToMeal(mealDtos.get(i), meals.get(i), locale);
            }
        }
    }

    public static void applyToMeal(MealDto dto, Meal entity, Locale locale) {
        if (dto == null || entity == null) return;
        String localeCode = useEnglish(locale) ? EN : "bg";
        MealTranslation t = entity.getTranslation(localeCode).orElse(entity.getDefaultTranslation());
        if (t != null) {
            dto.setName(t.getName());
            dto.setDescription(t.getDescription());
        }
    }

    public static void applyToFitnessProgram(FitnessProgramDto dto, FitnessProgram entity, Locale locale) {
        if (dto == null || entity == null) return;
        String localeCode = useEnglish(locale) ? EN : "bg";
        FitnessProgramTranslation t = entity.getTranslation(localeCode).orElse(entity.getDefaultTranslation());
        if (t != null) {
            dto.setName(t.getName());
            dto.setDescription(t.getDescription());
            dto.setBenefits(t.getBenefits());
        }
        if (dto.getExercises() != null && entity.getExercises() != null) {
            List<Exercise> exercises = entity.getExercises();
            List<ExerciseDto> exerciseDtos = dto.getExercises();
            for (int i = 0; i < Math.min(exercises.size(), exerciseDtos.size()); i++) {
                applyToExercise(exerciseDtos.get(i), exercises.get(i), locale);
            }
        }
    }

    public static void applyToExercise(ExerciseDto dto, Exercise entity, Locale locale) {
        if (dto == null || entity == null) return;
        String localeCode = useEnglish(locale) ? EN : "bg";
        ExerciseTranslation t = entity.getTranslation(localeCode).orElse(entity.getDefaultTranslation());
        if (t != null) {
            dto.setName(t.getName());
            dto.setDescription(t.getDescription());
            dto.setMuscleGroup(t.getMuscleGroup());
        }
    }
}
