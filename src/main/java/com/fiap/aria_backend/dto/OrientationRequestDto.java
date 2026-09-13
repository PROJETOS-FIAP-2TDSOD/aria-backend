package com.fiap.aria_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrientationRequestDto {

    private String code;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private String category;

    @NotNull
    private String priority;

    private String period;

    @NotEmpty
    private List<String> targetRoles;

    private List<KeyMetricInputDto> keyMetrics = new ArrayList<>();

    private String expiresAt; // formato yyyy-MM-dd, opcional

    @Data
    public static class KeyMetricInputDto {
        private String name;
        private String achieved;
        private String target;
        private float progress;
    }
}