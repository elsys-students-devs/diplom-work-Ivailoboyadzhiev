package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.DayOfWeek;
import com.myfitnessjourney.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByFitnessProgramId(Long fitnessProgramId);
    
    List<Exercise> findByFitnessProgramIdAndDayOfWeek(Long fitnessProgramId, DayOfWeek dayOfWeek);
}
