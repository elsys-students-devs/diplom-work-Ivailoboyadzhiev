package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.CompleteWorkoutRequest;
import com.myfitnessjourney.dto.CompletedDaysDto;
import com.myfitnessjourney.dto.CompletedWorkoutsCountDto;
import com.myfitnessjourney.entity.DayOfWeek;
import com.myfitnessjourney.service.CompletedWorkoutService;
import com.myfitnessjourney.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api/workouts")
@AllArgsConstructor
public class WorkoutCompletionController {

    private final CompletedWorkoutService completedWorkoutService;
    private final UserService userService;

    @PostMapping("/programs/{programId}/complete")
    public ResponseEntity<Void> completeWorkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long programId,
            @RequestBody CompleteWorkoutRequest request
    ) {
        if (userDetails == null || request == null || request.getDayOfWeek() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        completedWorkoutService.completeWorkout(userId, programId, request.getDayOfWeek());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<CompletedWorkoutsCountDto> getCompletedCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        long count = completedWorkoutService.getCompletedCount(userId);
        return ResponseEntity.ok(new CompletedWorkoutsCountDto(count));
    }

    @GetMapping("/programs/{programId}/completed-days")
    public ResponseEntity<CompletedDaysDto> getCompletedDaysForWeek(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long programId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        Set<DayOfWeek> days =
                completedWorkoutService.getCompletedDaysForWeek(userId, programId, weekStart);
        return ResponseEntity.ok(new CompletedDaysDto(days));
    }
}
