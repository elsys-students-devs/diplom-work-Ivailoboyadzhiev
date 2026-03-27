package com.myfitnessjourney.entity;

import jakarta.persistence.CascadeType;
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
import org.hibernate.annotations.BatchSize;

import java.util.List;
import java.util.Optional;

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

    @OneToMany(mappedBy = "fitnessProgram", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private List<FitnessProgramTranslation> translations;

    @OneToMany(mappedBy = "fitnessProgram", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exercise> exercises;

    public Optional<FitnessProgramTranslation> getTranslation(String locale) {
        if (translations == null || locale == null) return Optional.empty();
        return translations.stream()
                .filter(translation -> locale.equalsIgnoreCase(translation.getLocale()))
                .findFirst();
    }

    public FitnessProgramTranslation getDefaultTranslation() {
        if (translations == null || translations.isEmpty()) return null;
        return getTranslation("bg").orElse(translations.get(0));
    }
}
