package com.myfitnessjourney.dto.groq;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GroqChatResponse {
    private List<GroqChatChoice> choices;
}
