package com.myfitnessjourney.repository;

import com.myfitnessjourney.entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
    Optional<Diet> findFirstByTranslations_LocaleAndTranslations_Name(String locale, String name);

    @Query("SELECT DISTINCT d FROM Diet d LEFT JOIN FETCH d.meals")
    List<Diet> findAllWithMeals();

    @Query("SELECT d FROM Diet d LEFT JOIN FETCH d.meals WHERE d.id = :id")
    Optional<Diet> findByIdWithMeals(@Param("id") Long id);
}

