package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.CompletedWorkout;
import com.myfitnessjourney.entity.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompletedWorkoutRepository extends JpaRepository<CompletedWorkout, Long> {

    long countByUserId(Long userId);

    List<CompletedWorkout> findByUserIdAndFitnessProgramIdAndWeekStartDate(
            Long userId, Long fitnessProgramId, LocalDate weekStartDate);

    Optional<CompletedWorkout> findByUserIdAndFitnessProgramIdAndDayOfWeekAndWeekStartDate(
            Long userId, Long fitnessProgramId, DayOfWeek dayOfWeek, LocalDate weekStartDate);

    boolean existsByUserIdAndFitnessProgramIdAndDayOfWeekAndWeekStartDate(
            Long userId, Long fitnessProgramId, DayOfWeek dayOfWeek, LocalDate weekStartDate);
}
