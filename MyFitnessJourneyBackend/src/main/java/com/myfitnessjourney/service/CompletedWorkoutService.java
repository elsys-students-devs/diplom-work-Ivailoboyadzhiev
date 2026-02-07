package com.myfitnessjourney.service;

import com.myfitnessjourney.entity.CompletedWorkout;
import com.myfitnessjourney.entity.DayOfWeek;
import com.myfitnessjourney.entity.FitnessProgram;
import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.exception.FitnessProgramNotFoundException;
import com.myfitnessjourney.repository.CompletedWorkoutRepository;
import com.myfitnessjourney.repository.FitnessProgramRepository;
import com.myfitnessjourney.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompletedWorkoutService {

    private final CompletedWorkoutRepository completedWorkoutRepository;
    private final UserRepository userRepository;
    private final FitnessProgramRepository fitnessProgramRepository;

    private static LocalDate getWeekStart(LocalDate date) {
        return date.with(java.time.DayOfWeek.MONDAY);
    }

    @Transactional
    public void completeWorkout(Long userId, Long fitnessProgramId, DayOfWeek dayOfWeek) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        FitnessProgram program = fitnessProgramRepository.findById(fitnessProgramId)
                .orElseThrow(() -> new FitnessProgramNotFoundException("Fitness program not found"));
        LocalDate weekStart = getWeekStart(LocalDate.now());

        if (completedWorkoutRepository.existsByUserIdAndFitnessProgramIdAndDayOfWeekAndWeekStartDate(
                userId, fitnessProgramId, dayOfWeek, weekStart)) {
            return;
        }

        CompletedWorkout completed = new CompletedWorkout();
        completed.setUser(user);
        completed.setFitnessProgram(program);
        completed.setDayOfWeek(dayOfWeek);
        completed.setWeekStartDate(weekStart);
        completedWorkoutRepository.save(completed);
    }

    public long getCompletedCount(Long userId) {
        return completedWorkoutRepository.countByUserId(userId);
    }

    public Set<DayOfWeek> getCompletedDaysForWeek(Long userId, Long fitnessProgramId, LocalDate weekStart) {
        if (weekStart == null) {
            weekStart = getWeekStart(LocalDate.now());
        }
        List<CompletedWorkout> list = completedWorkoutRepository
                .findByUserIdAndFitnessProgramIdAndWeekStartDate(userId, fitnessProgramId, weekStart);
        return list.stream()
                .map(CompletedWorkout::getDayOfWeek)
                .collect(Collectors.toSet());
    }
}
