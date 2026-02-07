package com.myfitnessjourney.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GroqAiServiceTest {

    @Mock
    private FitnessProgramService fitnessProgramService;

    @Mock
    private DietService dietService;

    @Mock
    private GroqContextBuilder groqContextBuilder;

    private GroqAiService groqAiService;

    @BeforeEach
    void setUp() {
        groqAiService = new GroqAiService(
                fitnessProgramService,
                dietService,
                groqContextBuilder,
                "https://api.groq.com/openai/v1",
                "",
                "llama-3.1-8b-instant"
        );
    }

    @Test
    void generateResponse_whenApiKeyEmpty_returnsConfigErrorMessage() {
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
