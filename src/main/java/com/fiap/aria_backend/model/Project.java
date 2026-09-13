package com.fiap.aria_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    private String title;

    private String description;

    private String originIdeaId;

    private String managerId;

    private ProjectStatus status;

    @Builder.Default
    private int progress = 0;

    @Builder.Default
    private double estimatedRoi = 0.0;

    private Double actualRoi;

    @Builder.Default
    private double budget = 0.0;

    @Builder.Default
    private String sponsorLabel = "";

    @Builder.Default
    private String strategicOrientationLabel = "";

    @Builder.Default
    private List<ProjectTeamMember> teamMembers = new ArrayList<>();

    @Builder.Default
    private List<ProjectMilestone> milestones = new ArrayList<>();

    private LocalDate startDate;

    private LocalDate expectedEndDate;

    private LocalDateTime updatedAt;
}