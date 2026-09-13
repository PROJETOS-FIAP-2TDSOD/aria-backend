package com.fiap.aria_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDto {

    @NotNull
    private String userId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    private String type;

    private String relatedIdeaId;
}