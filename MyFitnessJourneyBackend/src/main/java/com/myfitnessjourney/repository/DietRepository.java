package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
    Optional<Diet> findByName(String name);
    
    @Query("SELECT DISTINCT d FROM Diet d LEFT JOIN FETCH d.meals")
    List<Diet> findAllWithMeals();
}

