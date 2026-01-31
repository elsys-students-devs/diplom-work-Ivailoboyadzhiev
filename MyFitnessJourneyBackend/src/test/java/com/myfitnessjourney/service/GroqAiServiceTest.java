package com.myfitnessjourney.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroqAiServiceTest {

    @Mock
    private FitnessProgramService fitnessProgramService;

    @Mock
    private DietService dietService;

    private GroqAiService groqAiService;

    @BeforeEach
    void setUp() {
        groqAiService = new GroqAiService(
                fitnessProgramService,
                dietService,
                "https://api.groq.com/openai/v1",
                "",
                "llama-3.1-8b-instant"
        );
    }

    @Test
    void generateResponse_whenApiKeyEmpty_returnsConfigErrorMessage() {
        when(fitnessProgramService.getAllFitnessProgramsWithExercises()).thenReturn(Collections.emptyList());
        when(dietService.getAllDietsWithMeals()).thenReturn(Collections.emptyList());

        String result = groqAiService.generateResponse("Здравей", "");

        assertThat(result).contains("не е конфигурирана");
    }

    @Test
    void generateResponse_whenApiKeyEmpty_doesNotCallDependencies() {
        String result = groqAiService.generateResponse("test", "");

        assertThat(result).isNotBlank();
        assertThat(result).contains("конфигурирана");
    }
}
