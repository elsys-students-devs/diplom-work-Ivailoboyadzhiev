package com.myfitnessjourney.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "fitness_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FitnessProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "description_en", length = 1000)
    private String descriptionEn;

    @Column(length = 500)
    private String benefits;

    @Column(name = "benefits_en", length = 500)
    private String benefitsEn;

    @OneToMany(mappedBy = "fitnessProgram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exercise> exercises;
}
