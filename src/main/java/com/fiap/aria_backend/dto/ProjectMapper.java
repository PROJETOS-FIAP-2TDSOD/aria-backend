package com.fiap.aria_backend.dto;

import com.fiap.aria_backend.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProjectMapper {

    private final IdeaMapper ideaMapper;

    public ProjectMapper(IdeaMapper ideaMapper) {
        this.ideaMapper = ideaMapper;
    }

    public ProjectResponseDto toResponseDto(Project project, Idea originIdea, User ideaAuthor,
                                            User manager, List<User> teamUsers) {
        return ProjectResponseDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .originIdea(ideaMapper.toResponseDto(originIdea, ideaAuthor))
                .manager(toUserSummary(manager))
                .status(project.getStatus().name())
                .progress(project.getProgress())
                .estimatedRoi(project.getEstimatedRoi())
                .actualRoi(project.getActualRoi())
                .budget(project.getBudget())
                .sponsorLabel(project.getSponsorLabel())
                .strategicOrientationLabel(project.getStrategicOrientationLabel())
                .teamMembers(mapTeamMembers(project.getTeamMembers(), teamUsers))
                .milestones(mapMilestones(project.getMilestones()))
                .startDate(project.getStartDate().toString())
                .expectedEndDate(project.getExpectedEndDate().toString())
                .updatedAt(project.getUpdatedAt().toString())
                .build();
    }

    private List<ProjectTeamMemberDto> mapTeamMembers(List<ProjectTeamMember> members, List<User> users) {
        return members.stream()
                .map(m -> ProjectTeamMemberDto.builder()
                        .user(toUserSummary(findUser(users, m.getUserId())))
                        .projectRole(m.getProjectRole())
                        .build())
                .toList();
    }

    private List<ProjectMilestoneDto> mapMilestones(List<ProjectMilestone> milestones) {
        return milestones.stream()
                .map(m -> ProjectMilestoneDto.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .dueDate(m.getDueDate().toString())
                        .status(m.getStatus().name())
                        .build())
                .toList();
    }

    private User findUser(List<User> users, String userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseGet(() -> User.builder()
                        .id(userId)
                        .name("Usuário removido")
                        .email("")
                        .role(UserRole.OPERADOR)
                        .department("")
                        .points(0)
                        .badges(new java.util.ArrayList<>())
                        .build());
    }

    private UserSummaryDto toUserSummary(User user) {
        return ideaMapper.toUserSummary(user);
    }

    public String generateMilestoneId() {
        return UUID.randomUUID().toString();
    }
}