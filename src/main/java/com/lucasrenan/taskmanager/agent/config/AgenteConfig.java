package com.lucasrenan.taskmanager.agent.config;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgenteConfig {

    @Bean
    public GoogleAiGeminiChatModel googleAiGeminiChatModel(
            @Value("${langchain4j.google-ai-gemini.api-key}") String apiKey,
            @Value("${langchain4j.google-ai-gemini.model-name}") String model) {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .build();
    }
}
