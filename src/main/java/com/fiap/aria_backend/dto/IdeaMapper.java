package com.fiap.aria_backend.dto;

import com.fiap.aria_backend.model.Idea;
import com.fiap.aria_backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class IdeaMapper {

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
                .createdAt(idea.getCreatedAt().toString())
                .updatedAt(idea.getUpdatedAt().toString())
                .build();
    }

    private UserSummaryDto toUserSummary(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .avatarInitials(user.getAvatarInitials())
                .totalIdeas(0)   // TODO: calcular quando a gamificação for implementada
                .approvedIdeas(0) // TODO: idem
                .build();
    }
}