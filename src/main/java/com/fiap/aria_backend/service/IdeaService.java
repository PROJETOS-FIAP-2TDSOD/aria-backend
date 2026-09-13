package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.*;
import com.fiap.aria_backend.model.Idea;
import com.fiap.aria_backend.model.IdeaCategory;
import com.fiap.aria_backend.model.IdeaStatus;
import com.fiap.aria_backend.model.User;
import com.fiap.aria_backend.repository.IdeaRepository;
import com.fiap.aria_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final IdeaMapper ideaMapper;

    public IdeaService(IdeaRepository ideaRepository, UserRepository userRepository, IdeaMapper ideaMapper) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.ideaMapper = ideaMapper;
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

    // TODO: authorId deve vir do usuário autenticado (JWT), não de parâmetro solto —
    // ajustar assim que a segurança JWT (feat/spring-security-jwt) estiver pronta.
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

    public IdeaResponseDto update(String id, IdeaRequestDto request) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada: " + id));

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

    public void delete(String id) {
        ideaRepository.deleteById(id);
    }

    private IdeaResponseDto toResponse(Idea idea) {
        User author = userRepository.findById(idea.getAuthorId())
                .orElseThrow(() -> new IllegalStateException("Autor não encontrado para a ideia: " + idea.getId()));
        return ideaMapper.toResponseDto(idea, author);
    }
}