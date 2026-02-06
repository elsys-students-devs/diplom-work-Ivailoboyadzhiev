package com.myfitnessjourney.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "meals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private List<MealTranslation> translations;

    @Column(nullable = false)
    private Double calories;

    @Column(nullable = false)
    private Double protein; // grams

    @Column(nullable = false)
    private Double carbs; // grams

    @Column(nullable = false)
    private Double fat; // grams

    @Column
    private Double fiber; // grams

    @Column
    private Double sugar; // grams

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id", nullable = false)
    private Diet diet;

    public Optional<MealTranslation> getTranslation(String locale) {
        if (translations == null || locale == null) return Optional.empty();
        return translations.stream()
                .filter(t -> locale.equalsIgnoreCase(t.getLocale()))
                .findFirst();
    }

    public MealTranslation getDefaultTranslation() {
        if (translations == null || translations.isEmpty()) return null;
        return getTranslation("bg").orElse(translations.get(0));
    }
}

