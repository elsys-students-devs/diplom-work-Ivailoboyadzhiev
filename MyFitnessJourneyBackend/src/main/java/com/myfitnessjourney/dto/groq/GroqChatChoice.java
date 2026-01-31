package com.myfitnessjourney.dto.groq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroqChatChoice {
    private GroqChatMessage message;
    private Integer index;
    @JsonProperty("finish_reason")
    private String finishReason;
}
