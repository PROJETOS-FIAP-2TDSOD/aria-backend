package com.fiap.aria_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private String originIdeaId;

    @NotBlank
    private String sponsorLabel;

    @NotBlank
    private String strategicOrientationLabel;

    private double budget;

    private double estimatedRoi;

    private List<TeamMemberInputDto> teamMembers = new ArrayList<>();

    private List<MilestoneInputDto> milestones = new ArrayList<>();

    @NotNull
    private String startDate; // formato yyyy-MM-dd

    @NotNull
    private String expectedEndDate;

    @Data
    public static class TeamMemberInputDto {
        private String userId;
        private String projectRole;
    }

    @Data
    public static class MilestoneInputDto {
        private String title;
        private String dueDate; // yyyy-MM-dd
    }
}