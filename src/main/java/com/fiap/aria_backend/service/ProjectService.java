package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.*;
import com.fiap.aria_backend.model.*;
import com.fiap.aria_backend.repository.IdeaRepository;
import com.fiap.aria_backend.repository.ProjectRepository;
import com.fiap.aria_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, IdeaRepository ideaRepository,
                          UserRepository userRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
    }

    public List<ProjectResponseDto> listAll() {
        return projectRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProjectResponseDto findById(String id) {
        return toResponse(getOrThrow(id));
    }

    public ProjectResponseDto create(ProjectRequestDto request, String managerId) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .originIdeaId(request.getOriginIdeaId())
                .managerId(managerId)
                .status(ProjectStatus.PLANEJAMENTO)
                .progress(0)
                .budget(request.getBudget())
                .estimatedRoi(request.getEstimatedRoi())
                .sponsorLabel(request.getSponsorLabel())
                .strategicOrientationLabel(request.getStrategicOrientationLabel())
                .teamMembers(buildTeamMembers(request))
                .milestones(buildMilestones(request))
                .startDate(LocalDate.parse(request.getStartDate()))
                .expectedEndDate(LocalDate.parse(request.getExpectedEndDate()))
                .updatedAt(LocalDateTime.now())
                .build();

        return toResponse(projectRepository.save(project));
    }

    public ProjectResponseDto update(String id, ProjectRequestDto request) {
        Project project = getOrThrow(id);

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setSponsorLabel(request.getSponsorLabel());
        project.setStrategicOrientationLabel(request.getStrategicOrientationLabel());
        project.setBudget(request.getBudget());
        project.setEstimatedRoi(request.getEstimatedRoi());
        project.setTeamMembers(buildTeamMembers(request));
        project.setMilestones(buildMilestones(request));
        project.setStartDate(LocalDate.parse(request.getStartDate()));
        project.setExpectedEndDate(LocalDate.parse(request.getExpectedEndDate()));
        project.setUpdatedAt(LocalDateTime.now());

        return toResponse(projectRepository.save(project));
    }

    private List<ProjectTeamMember> buildTeamMembers(ProjectRequestDto request) {
        return request.getTeamMembers().stream()
                .map(t -> ProjectTeamMember.builder().userId(t.getUserId()).projectRole(t.getProjectRole()).build())
                .toList();
    }

    private List<ProjectMilestone> buildMilestones(ProjectRequestDto request) {
        return request.getMilestones().stream()
                .map(m -> ProjectMilestone.builder()
                        .id(projectMapper.generateMilestoneId())
                        .title(m.getTitle())
                        .dueDate(LocalDate.parse(m.getDueDate()))
                        .status(m.getStatus() != null ? MilestoneStatus.valueOf(m.getStatus()) : MilestoneStatus.PENDING)
                        .build())
                .toList();
    }

    // Atualização de progresso/status/ROI real — ação recorrente do gestor
    public ProjectResponseDto updateProgress(String id, ProjectUpdateProgressDto dto) {
        Project project = getOrThrow(id);

        if (dto.getStatus() != null) project.setStatus(ProjectStatus.valueOf(dto.getStatus()));
        if (dto.getProgress() != null) project.setProgress(dto.getProgress());
        if (dto.getActualRoi() != null) project.setActualRoi(dto.getActualRoi());
        project.setUpdatedAt(LocalDateTime.now());

        return toResponse(projectRepository.save(project));
    }

    public void delete(String id) {
        projectRepository.deleteById(id);
    }

    private Project getOrThrow(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado: " + id));
    }

    private ProjectResponseDto toResponse(Project project) {
        Idea idea = ideaRepository.findById(project.getOriginIdeaId())
                .orElseGet(() -> deletedIdeaPlaceholder(project.getOriginIdeaId()));
        User ideaAuthor = userRepository.findById(idea.getAuthorId())
                .orElseGet(() -> deletedUserPlaceholder(idea.getAuthorId()));
        User manager = userRepository.findById(project.getManagerId())
                .orElseGet(() -> deletedUserPlaceholder(project.getManagerId()));
        List<User> teamUsers = userRepository.findAllById(
                project.getTeamMembers().stream().map(ProjectTeamMember::getUserId).toList());

        return projectMapper.toResponseDto(project, idea, ideaAuthor, manager, teamUsers);
    }

    private Idea deletedIdeaPlaceholder(String ideaId) {
        return Idea.builder()
                .id(ideaId)
                .title("Ideia removida")
                .authorId("")
                .category(IdeaCategory.PROCESSO)
                .description("")
                .problema("")
                .beneficios("")
                .recursos("")
                .status(IdeaStatus.EM_PROJETO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User deletedUserPlaceholder(String userId) {
        return User.builder()
                .id(userId)
                .name("Usuário removido")
                .email("")
                .role(UserRole.OPERADOR)
                .department("")
                .points(0)
                .badges(new ArrayList<>())
                .build();
    }
}