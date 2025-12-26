package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.UserLoggedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserLoggedMealRepository extends JpaRepository<UserLoggedMeal, Long> {
    List<UserLoggedMeal> findByUserIdAndLoggedDate(Long userId, LocalDate date);
    
    @Query("SELECT ulm FROM UserLoggedMeal ulm JOIN FETCH ulm.meal WHERE ulm.user.id = :userId AND ulm.loggedDate = :date")
    List<UserLoggedMeal> findTodayMealsByUserId(@Param("userId") Long userId, @Param("date") LocalDate date);
}

