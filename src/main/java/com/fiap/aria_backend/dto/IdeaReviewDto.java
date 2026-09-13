package com.fiap.aria_backend.dto;

import lombok.Data;

@Data
public class IdeaReviewDto {
    private String status;
    private Integer score;
    private String gestorFeedback;
}