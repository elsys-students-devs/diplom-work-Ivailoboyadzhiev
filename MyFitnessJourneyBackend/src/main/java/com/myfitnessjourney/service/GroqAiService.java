package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.DietDto;
import com.myfitnessjourney.dto.FitnessProgramDto;
import com.myfitnessjourney.dto.groq.GroqChatMessage;
import com.myfitnessjourney.dto.groq.GroqChatRequest;
import com.myfitnessjourney.dto.groq.GroqChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class GroqAiService {

    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final int MAX_TOKENS = 1000;
    private static final int LOG_MESSAGE_PREVIEW_LENGTH = 50;

    private final WebClient webClient;
    private final FitnessProgramService fitnessProgramService;
    private final DietService dietService;
    private final GroqContextBuilder groqContextBuilder;
    private final String apiKey;
    private final String baseUrl;
    private final String model;

    public GroqAiService(
            FitnessProgramService fitnessProgramService,
            DietService dietService,
            GroqContextBuilder groqContextBuilder,
            @Value("${groq.api.base-url:https://api.groq.com/openai/v1}") String baseUrl,
            @Value("${groq.api.key:}") String apiKey,
            @Value("${groq.api.model:llama-3.1-8b-instant}") String model) {
        this.fitnessProgramService = fitnessProgramService;
        this.dietService = dietService;
        this.groqContextBuilder = groqContextBuilder;
        this.baseUrl = baseUrl;
        this.model = model;
        
        // Get API key from config or environment variable
        String finalApiKey = apiKey != null && !apiKey.isEmpty() 
            ? apiKey 
            : System.getenv("GROQ_API_KEY");
        
        if (finalApiKey == null || finalApiKey.isEmpty()) {
            log.warn("Groq API key not configured. AI features will not work.");
        }
        
        this.apiKey = finalApiKey != null ? finalApiKey : "";
        
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + this.apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String generateResponse(String userMessage, String conversationHistory) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("Groq API key is not configured! Please set GROQ_API_KEY environment variable or groq.api.key in application.yml");
            return "Извинявам се, AI услугата не е конфигурирана. Моля свържете се с администратора.";
        }
        
        try {
            log.info("Generating AI response for message: {}", userMessage.substring(0, Math.min(LOG_MESSAGE_PREVIEW_LENGTH, userMessage.length())));
            
            // Get fitness programs and diets data
            log.debug("Fetching fitness programs and diets...");
            List<FitnessProgramDto> fitnessPrograms = fitnessProgramService.getAllFitnessProgramsWithExercises();
            List<DietDto> diets = dietService.getAllDietsWithMeals();
            log.debug("Found {} fitness programs and {} diets", fitnessPrograms.size(), diets.size());

            // Build context with available data (cached by program/diet IDs)
            String context = groqContextBuilder.buildContext(fitnessPrograms, diets);
            log.debug("Context built, length: {}", context.length());

            // Build system prompt
            String systemPrompt = buildSystemPrompt(context);

            // Build messages for API
            GroqChatRequest request = new GroqChatRequest();
            request.setModel(model);
            String userPrompt = conversationHistory.isEmpty() 
                ? userMessage 
                : conversationHistory + "\n\nUser: " + userMessage + "\n\nAssistant:";
            
            request.setMessages(List.of(
                    new GroqChatMessage("system", systemPrompt),
                    new GroqChatMessage("user", userPrompt)
            ));
            request.setTemperature(DEFAULT_TEMPERATURE);
            request.setMaxTokens(MAX_TOKENS);

            log.info("Calling Groq API at {} with model {}", baseUrl, model);

            GroqChatResponse response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                        clientResponse -> {
                            log.error("Groq API HTTP error: Status {}", clientResponse.statusCode());
                            clientResponse.bodyToMono(String.class)
                                .subscribe(body -> log.error("Groq API error body: {}", body));
                            return Mono.error(new RuntimeException("Groq API error: " + clientResponse.statusCode()));
                        })
                    .bodyToMono(GroqChatResponse.class)
                    .doOnError(error -> log.error("Error in WebClient call to Groq API: {}", error.getMessage(), error))
                    .block();

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                log.info("AI response received successfully, length: {}", content.length());
                return content;
            }

            log.warn("Groq API returned empty response");
            return "Извинявам се, не мога да отговоря в момента. Моля опитайте отново.";

        } catch (org.springframework.web.reactive.function.client.WebClientException e) {
            log.error("WebClient error calling Groq API: {}", e.getMessage(), e);
            return "Извинявам се, не мога да се свържа с AI услугата. Моля опитайте отново по-късно.";
        } catch (Exception e) {
            log.error("Unexpected error calling Groq API: {}", e.getMessage(), e);
            return "Извинявам се, възникна неочаквана грешка. Моля опитайте отново по-късно.";
        }
    }

    private String buildSystemPrompt(String context) {
        return """
            Ти си Groc, AI фитнес асистент в приложението MyFitnessJourney.
            Твоята роля е да помагаш на потребителите с въпроси относно фитнес програми и диети.
            Отговаряй на български език, бъди полезен, приятелски и професионален.

            Имаш достъп до следната информация:

            %s

            Когато потребителят пита за конкретна фитнес програма или диета, използвай предоставената информация.
            Ако не знаеш нещо, признай го честно. Винаги бъди полезен и подкрепящ.
            """.formatted(context);
    }

}
