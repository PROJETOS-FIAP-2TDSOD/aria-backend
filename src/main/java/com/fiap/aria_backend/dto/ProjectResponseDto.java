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
public class ProjectResponseDto {
    private String id;
    private String title;
    private String description;
    private IdeaResponseDto originIdea;
    private UserSummaryDto manager;
    private String status;
    private int progress;
    private double estimatedRoi;
    private Double actualRoi;
    private double budget;
    private String sponsorLabel;
    private String strategicOrientationLabel;
    private List<ProjectTeamMemberDto> teamMembers;
    private List<ProjectMilestoneDto> milestones;
    private String startDate;
    private String expectedEndDate;
    private String updatedAt;
}