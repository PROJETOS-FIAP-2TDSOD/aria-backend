package com.fiap.aria_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaResponseDto {
    private String id;
    private String title;
    private UserSummaryDto author;
    private String category;
    private String description;
    private String problema;
    private String beneficios;
    private String recursos;
    private String status;
    private Integer score;
    private String gestorFeedback;
    private Double estimatedRoi;
    private String createdAt;
    private String updatedAt;
}