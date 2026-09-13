package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.*;
import com.fiap.aria_backend.model.*;
import com.fiap.aria_backend.repository.OrientationRepository;
import com.fiap.aria_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrientationService {

    private final OrientationRepository orientationRepository;
    private final UserRepository userRepository;
    private final OrientationMapper orientationMapper;
    private final OrientationEnrichmentService enrichmentService;

    public OrientationService(OrientationRepository orientationRepository, UserRepository userRepository,
                              OrientationMapper orientationMapper, OrientationEnrichmentService enrichmentService) {
        this.orientationRepository = orientationRepository;
        this.userRepository = userRepository;
        this.orientationMapper = orientationMapper;
        this.enrichmentService = enrichmentService;
    }

    public List<OrientationResponseDto> listAll() {
        return orientationRepository.findAll().stream().map(this::toResponse).toList();
    }

    public OrientationResponseDto findById(String id) {
        return toResponse(getOrThrow(id));
    }

    // TODO: authorId deve vir do usuário autenticado (JWT/LIDER)
    public OrientationResponseDto create(OrientationRequestDto request, String authorId) {
        List<OrientationKeyMetric> keyMetrics = request.getKeyMetrics().stream()
                .map(k -> OrientationKeyMetric.builder()
                        .name(k.getName()).achieved(k.getAchieved()).target(k.getTarget()).progress(k.getProgress())
                        .build())
                .toList();

        Orientation orientation = Orientation.builder()
                .code(request.getCode())
                .title(request.getTitle())
                .description(request.getDescription())
                .authorId(authorId)
                .category(IdeaCategory.valueOf(request.getCategory()))
                .priority(OrientationPriority.valueOf(request.getPriority()))
                .period(request.getPeriod())
                .targetRoles(request.getTargetRoles().stream().map(UserRole::valueOf).toList())
                .keyMetrics(keyMetrics)
                .createdAt(LocalDateTime.now())
                .expiresAt(request.getExpiresAt() != null ? LocalDate.parse(request.getExpiresAt()).atStartOfDay() : null)
                .build();

        return toResponse(orientationRepository.save(orientation));
    }

    public OrientationResponseDto update(String id, OrientationRequestDto request) {
        Orientation orientation = getOrThrow(id);

        List<OrientationKeyMetric> keyMetrics = request.getKeyMetrics().stream()
                .map(k -> OrientationKeyMetric.builder()
                        .name(k.getName()).achieved(k.getAchieved()).target(k.getTarget()).progress(k.getProgress())
                        .build())
                .toList();

        orientation.setCode(request.getCode());
        orientation.setTitle(request.getTitle());
        orientation.setDescription(request.getDescription());
        orientation.setCategory(IdeaCategory.valueOf(request.getCategory()));
        orientation.setPriority(OrientationPriority.valueOf(request.getPriority()));
        orientation.setPeriod(request.getPeriod());
        orientation.setTargetRoles(request.getTargetRoles().stream().map(UserRole::valueOf).toList());
        orientation.setKeyMetrics(keyMetrics);
        orientation.setExpiresAt(request.getExpiresAt() != null ? LocalDate.parse(request.getExpiresAt()).atStartOfDay() : null);

        return toResponse(orientationRepository.save(orientation));
    }

    public void delete(String id) {
        orientationRepository.deleteById(id);
    }

    private Orientation getOrThrow(String id) {
        return orientationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orientação não encontrada: " + id));
    }

    private OrientationResponseDto toResponse(Orientation orientation) {
        User author = userRepository.findById(orientation.getAuthorId())
                .orElseThrow(() -> new IllegalStateException("Autor não encontrado"));
        var metrics = enrichmentService.enrich(orientation);
        return orientationMapper.toResponseDto(orientation, author, metrics);
    }
}