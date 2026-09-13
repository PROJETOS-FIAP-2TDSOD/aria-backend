package com.fiap.aria_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orientations")
public class Orientation {

    @Id
    private String id;

    private String code;

    private String title;

    private String description;

    private String authorId;

    private IdeaCategory category;

    private OrientationPriority priority;

    private String period;

    private List<UserRole> targetRoles;

    @Builder.Default
    private List<OrientationKeyMetric> keyMetrics = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    // Observação: ideasCount, ideasDelta, projectsActive, roiCompact, roiDeltaPercent e progress
    // NÃO são persistidos aqui — são calculados dinamicamente pelo OrientationEnrichmentService.
}