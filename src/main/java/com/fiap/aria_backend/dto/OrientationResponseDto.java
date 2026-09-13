package com.fiap.aria_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrientationResponseDto {
    private String id;
    private String code;
    private String title;
    private String description;
    private UserSummaryDto author;
    private String category;
    private String priority;
    private String period;
    private List<String> targetRoles;
    private List<OrientationKeyMetricDto> keyMetrics;
    private int ideasCount;
    private int ideasDelta;
    private int projectsActive;
    private String roiCompact;
    private int roiDeltaPercent;
    private float progress;
    private String createdAt;
    private String expiresAt;
}