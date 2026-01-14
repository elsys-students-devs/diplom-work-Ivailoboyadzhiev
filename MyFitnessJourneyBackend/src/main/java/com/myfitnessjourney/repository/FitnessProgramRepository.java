package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.FitnessProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FitnessProgramRepository extends JpaRepository<FitnessProgram, Long> {
    Optional<FitnessProgram> findByName(String name);
    
    @Query("SELECT DISTINCT fp FROM FitnessProgram fp LEFT JOIN FETCH fp.exercises")
    List<FitnessProgram> findAllWithExercises();
    
    @Query("SELECT fp FROM FitnessProgram fp LEFT JOIN FETCH fp.exercises WHERE fp.id = :id")
    Optional<FitnessProgram> findByIdWithExercises(Long id);
    
    boolean existsById(Long id);
}
