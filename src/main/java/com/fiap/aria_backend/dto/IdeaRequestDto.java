package com.fiap.aria_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdeaRequestDto {

    @NotBlank
    private String title;

    @NotNull
    private String category;

    @NotBlank
    private String description;

    @NotBlank
    private String problema;

    @NotBlank
    private String beneficios;

    private String recursos;
}