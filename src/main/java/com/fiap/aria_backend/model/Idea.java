package com.fiap.aria_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ideas")
public class Idea {

    @Id
    private String id;

    private String title;

    private String authorId;

    private IdeaCategory category;

    private String description;

    private String problema;

    private String beneficios;

    private String recursos;

    private IdeaStatus status;

    private Integer score;

    private String gestorFeedback;

    private Double estimatedRoi;

    // Preenchidos pelo endpoint /ai-score — sugestao da IA, nao substitui score/gestorFeedback
    private Integer aiScore;

    private String aiJustification;

    private LocalDateTime aiAnalyzedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}