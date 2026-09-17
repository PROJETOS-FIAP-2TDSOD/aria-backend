package com.fiap.aria_backend.dto;

import com.fiap.aria_backend.model.Idea;
import com.fiap.aria_backend.model.IdeaStatus;
import com.fiap.aria_backend.model.User;
import com.fiap.aria_backend.repository.IdeaRepository;
import com.fiap.aria_backend.service.GamificationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IdeaMapper {

    private final IdeaRepository ideaRepository;
    private final GamificationService gamificationService;

    public IdeaMapper(IdeaRepository ideaRepository, GamificationService gamificationService) {
        this.ideaRepository = ideaRepository;
        this.gamificationService = gamificationService;
    }

    public IdeaResponseDto toResponseDto(Idea idea, User author) {
        return IdeaResponseDto.builder()
                .id(idea.getId())
                .title(idea.getTitle())
                .author(toUserSummary(author))
                .category(idea.getCategory().name())
                .description(idea.getDescription())
                .problema(idea.getProblema())
                .beneficios(idea.getBeneficios())
                .recursos(idea.getRecursos())
                .status(idea.getStatus().name())
                .score(idea.getScore())
                .gestorFeedback(idea.getGestorFeedback())
                .estimatedRoi(idea.getEstimatedRoi())
                .aiScore(idea.getAiScore())
                .aiJustification(idea.getAiJustification())
                .aiAnalyzedAt(idea.getAiAnalyzedAt() != null ? idea.getAiAnalyzedAt().toString() : null)
                .createdAt(idea.getCreatedAt().toString())
                .updatedAt(idea.getUpdatedAt().toString())
                .build();
    }

    // Publico: tambem usado pelo AuthService para montar o resumo do usuario no login/registro
    public UserSummaryDto toUserSummary(User user) {
        List<Idea> ideas = ideaRepository.findByAuthorId(user.getId());
        int approvedIdeas = (int) ideas.stream().filter(i -> i.getStatus() == IdeaStatus.APROVADA).count();

        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .avatarInitials(user.getAvatarInitials())
                .totalIdeas(ideas.size())
                .approvedIdeas(approvedIdeas)
                .points(gamificationService.calculatePoints(ideas))
                .badges(gamificationService.calculateBadgeIds(ideas))
                .build();
    }
}