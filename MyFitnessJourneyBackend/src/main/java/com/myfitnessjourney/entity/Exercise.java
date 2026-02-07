package com.myfitnessjourney.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private List<ExerciseTranslation> translations;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_program_id", nullable = false)
    private FitnessProgram fitnessProgram;

    /** Returns translation for the given locale (e.g. "bg", "en", "de"). */
    public Optional<ExerciseTranslation> getTranslation(String locale) {
        if (translations == null || locale == null) return Optional.empty();
        return translations.stream()
                .filter(t -> locale.equalsIgnoreCase(t.getLocale()))
                .findFirst();
    }

    /** Fallback when requested locale is missing: prefer "bg", then first available. */
    public ExerciseTranslation getDefaultTranslation() {
        if (translations == null || translations.isEmpty()) return null;
        return getTranslation("bg").orElse(translations.get(0));
    }
}
