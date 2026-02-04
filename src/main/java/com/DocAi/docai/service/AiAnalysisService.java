package com.DocAi.docai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiAnalysisService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAnalysisService(@Value("${openai.api.key}") String openAiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + openAiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String analyze(String pdfText, String analysisType) {
        String prompt;

        if ("SUMMARY".equalsIgnoreCase(analysisType)) {
            prompt = "Summarize the following document:\n" + pdfText;
        } else if ("KEY_POINTS".equalsIgnoreCase(analysisType)) {
            prompt = "Carefully read the following document and extract the top 5–10 insights that are most important for understanding the content. Present them as concise bullet points:\n" + pdfText;
        } else {
            return "Invalid analysis type.";
        }

        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(Map.of(
                            "model", "gpt-3.5-turbo",
                            "messages", List.of(
                                    Map.of("role", "system", "content", "You are a helpful assistant."),
                                    Map.of("role", "user", "content", prompt)
                            ),
                            "temperature", 0.7
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests))
                    .onErrorResume(WebClientResponseException.TooManyRequests.class, e -> {
                        log.warn("OpenAI rate limit exceeded: {}", e.getMessage());
                        return Mono.just("{\"error\":\"OpenAI rate limit exceeded. Try again later.\"}");
                    })
                    .onErrorResume(e -> {
                        log.error("Error calling OpenAI API: {}", e.getMessage(), e);
                        return Mono.just("{\"error\":\"Error analyzing document.\"}");
                    })
                    .block();

            // Parse JSON to extract the assistant message
            JsonNode root = objectMapper.readTree(response);
            if (root.has("choices") && root.get("choices").isArray()) {
                JsonNode choice = root.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    return choice.get("message").get("content").asText();
                }
            }

            return "No valid response from OpenAI.";

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return "Unexpected error occurred during analysis.";
        }
    }
}
