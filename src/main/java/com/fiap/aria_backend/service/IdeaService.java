package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.*;
import com.fiap.aria_backend.exception.AccessDeniedException;
import com.fiap.aria_backend.model.Idea;
import com.fiap.aria_backend.model.IdeaCategory;
import com.fiap.aria_backend.model.IdeaStatus;
import com.fiap.aria_backend.model.User;
import com.fiap.aria_backend.model.UserRole;
import com.fiap.aria_backend.repository.IdeaRepository;
import com.fiap.aria_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final IdeaMapper ideaMapper;
    private final GeminiIdeaScoringService geminiIdeaScoringService;

    public IdeaService(IdeaRepository ideaRepository, UserRepository userRepository, IdeaMapper ideaMapper,
                       GeminiIdeaScoringService geminiIdeaScoringService) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.ideaMapper = ideaMapper;
        this.geminiIdeaScoringService = geminiIdeaScoringService;
    }

    public List<IdeaResponseDto> listAll() {
        return ideaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public IdeaResponseDto findById(String id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada: " + id));
        return toResponse(idea);
    }

    public List<IdeaResponseDto> listByAuthor(String authorId) {
        return ideaRepository.findByAuthorId(authorId).stream()
                .map(this::toResponse)
                .toList();
    }

    public IdeaResponseDto create(IdeaRequestDto request, String authorId) {
        Idea idea = Idea.builder()
                .title(request.getTitle())
                .authorId(authorId)
                .category(IdeaCategory.valueOf(request.getCategory()))
                .description(request.getDescription())
                .problema(request.getProblema())
                .beneficios(request.getBeneficios())
                .recursos(request.getRecursos())
                .status(IdeaStatus.AGUARDANDO_ANALISE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toResponse(ideaRepository.save(idea));
    }

    public IdeaResponseDto update(String id, IdeaRequestDto request, String currentUserId) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada: " + id));

        if (!idea.getAuthorId().equals(currentUserId)) {
            throw new AccessDeniedException("Você só pode editar as próprias ideias.");
        }

        idea.setTitle(request.getTitle());
        idea.setCategory(IdeaCategory.valueOf(request.getCategory()));
        idea.setDescription(request.getDescription());
        idea.setProblema(request.getProblema());
        idea.setBeneficios(request.getBeneficios());
        idea.setRecursos(request.getRecursos());
        idea.setUpdatedAt(LocalDateTime.now());

        return toResponse(ideaRepository.save(idea));
    }

    // Ação exclusiva do GESTOR — aprovar/rejeitar/pontuar
    public IdeaResponseDto review(String id, IdeaReviewDto reviewDto) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada: " + id));

        idea.setStatus(IdeaStatus.valueOf(reviewDto.getStatus()));
        idea.setScore(reviewDto.getScore());
        idea.setGestorFeedback(reviewDto.getGestorFeedback());
        idea.setUpdatedAt(LocalDateTime.now());

        return toResponse(ideaRepository.save(idea));
    }

    // Ação exclusiva do GESTOR — sugestão de pontuação via IA (nao substitui review())
    public IdeaResponseDto scoreWithAi(String id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada: " + id));

        GeminiIdeaScoringService.AiScoreResult result = geminiIdeaScoringService.scoreIdea(idea);

        idea.setAiScore(result.score());
        idea.setAiJustification(result.justification());
        idea.setAiAnalyzedAt(LocalDateTime.now());
        idea.setUpdatedAt(LocalDateTime.now());

        return toResponse(ideaRepository.save(idea));
    }

    public void delete(String id, String currentUserId) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada: " + id));

        if (!idea.getAuthorId().equals(currentUserId)) {
            throw new AccessDeniedException("Você só pode excluir as próprias ideias.");
        }

        ideaRepository.deleteById(id);
    }

    private IdeaResponseDto toResponse(Idea idea) {
        User author = userRepository.findById(idea.getAuthorId())
                .orElseGet(() -> deletedUserPlaceholder(idea.getAuthorId(), idea.getId()));
        return ideaMapper.toResponseDto(idea, author);
    }

    private User deletedUserPlaceholder(String authorId, String ideaId) {
        log.warn("Autor {} nao encontrado para a ideia {} - usando placeholder.", authorId, ideaId);
        return User.builder()
                .id(authorId)
                .name("Usuário removido")
                .email("")
                .role(UserRole.OPERADOR)
                .department("")
                .points(0)
                .badges(new ArrayList<>())
                .build();
    }
}