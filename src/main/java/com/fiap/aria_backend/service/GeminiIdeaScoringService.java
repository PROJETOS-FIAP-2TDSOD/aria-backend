package com.fiap.aria_backend.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.fiap.aria_backend.exception.AiServiceException;
import com.fiap.aria_backend.model.Idea;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiIdeaScoringService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiIdeaScoringService(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-2.0-flash}") String model,
            ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(15));

        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .requestFactory(requestFactory)
                .build();
    }

    public AiScoreResult scoreIdea(Idea idea) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiServiceException("GEMINI_API_KEY nao configurada no ambiente.");
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", buildPrompt(idea))))),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", Map.of(
                                "type", "OBJECT",
                                "properties", Map.of(
                                        "score", Map.of("type", "INTEGER"),
                                        "justification", Map.of("type", "STRING")
                                ),
                                "required", List.of("score", "justification")
                        )
                )
        );

        String responseBody;
        try {
            responseBody = restClient.post()
                    .uri("/models/{model}:generateContent?key={key}", model, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            throw new AiServiceException("Falha ao chamar a Gemini API: " + e.getMessage());
        }

        return parseResponse(responseBody);
    }

    private String buildPrompt(Idea idea) {
        return """
                Você é um avaliador de ideias de inovação corporativa de uma empresa de transporte e logística.
                Avalie a ideia abaixo e retorne um score de 0 a 100 (potencial de impacto e viabilidade)
                e uma justificativa curta (no máximo 2 frases, em português).

                Título: %s
                Categoria: %s
                Descrição: %s
                Problema que resolve: %s
                Benefícios esperados: %s
                Recursos necessários: %s
                """.formatted(
                idea.getTitle(),
                idea.getCategory(),
                idea.getDescription(),
                idea.getProblema(),
                idea.getBeneficios(),
                idea.getRecursos()
        );
    }

    private AiScoreResult parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root.path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text").asText();

            if (text.isBlank()) {
                throw new AiServiceException("Resposta vazia da Gemini API.");
            }

            JsonNode result = objectMapper.readTree(text);
            int score = Math.max(0, Math.min(100, result.path("score").asInt()));
            String justification = result.path("justification").asText();

            return new AiScoreResult(score, justification);
        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AiServiceException("Nao foi possivel interpretar a resposta da Gemini API.");
        }
    }

    public record AiScoreResult(int score, String justification) {
    }
}