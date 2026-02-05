package com.myfitnessjourney.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(length = 500)
    private String description;

    @Column(name = "description_en", length = 500)
    private String descriptionEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @Column
    private Integer sets;

    @Column
    private Integer reps;

    @Column
    private BigDecimal weight; 

    @Column(length = 50)
    private String weightUnit;

    @Column(name = "muscle_group", length = 100)
    private String muscleGroup; // e.g., "Chest", "Back", "Legs"

    @Column(name = "muscle_group_en", length = 100)
    private String muscleGroupEnglish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_program_id", nullable = false)
    private FitnessProgram fitnessProgram;
}
