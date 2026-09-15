package com.fiap.aria_backend.service;

import com.fiap.aria_backend.model.*;
import com.fiap.aria_backend.repository.IdeaRepository;
import com.fiap.aria_backend.repository.ProjectRepository;
import com.fiap.aria_backend.util.CurrencyFormatter;
import com.fiap.aria_backend.util.RoiAnalytics;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrientationEnrichmentService {

    private final IdeaRepository ideaRepository;
    private final ProjectRepository projectRepository;

    public OrientationEnrichmentService(IdeaRepository ideaRepository, ProjectRepository projectRepository) {
        this.ideaRepository = ideaRepository;
        this.projectRepository = projectRepository;
    }

    public EnrichedMetrics enrich(Orientation orientation) {
        List<Idea> alignedIdeas = ideaRepository.findAll().stream()
                .filter(i -> i.getCategory() == orientation.getCategory())
                .toList();

        List<Project> allProjects = projectRepository.findAll();
        List<Project> alignedProjects = allProjects.stream()
                .filter(p -> {
                    Idea origin = ideaRepository.findById(p.getOriginIdeaId()).orElse(null);
                    return origin != null && origin.getCategory() == orientation.getCategory();
                })
                .toList();

        int approved = (int) alignedIdeas.stream()
                .filter(i -> i.getStatus() == IdeaStatus.APROVADA || i.getStatus() == IdeaStatus.EM_PROJETO)
                .count();

        float progress = alignedIdeas.isEmpty()
                ? 0f
                : Math.min(1f, Math.max(0f, (float) approved / alignedIdeas.size()));

        double totalRoi = RoiAnalytics.totalRoi(alignedProjects);

        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        int ideasDelta = (int) alignedIdeas.stream()
                .filter(i -> i.getCreatedAt().isAfter(cutoff))
                .count();

        int projectsActive = (int) alignedProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.EM_ANDAMENTO)
                .count();

        int roiDeltaPercent = RoiAnalytics.deltaPercentLast30Days(alignedProjects);

        return new EnrichedMetrics(
                alignedIdeas.size(),
                ideasDelta,
                projectsActive,
                CurrencyFormatter.toCompactReais(totalRoi),
                roiDeltaPercent,
                progress
        );
    }

    public record EnrichedMetrics(
            int ideasCount,
            int ideasDelta,
            int projectsActive,
            String roiCompact,
            int roiDeltaPercent,
            float progress
    ) {}
}