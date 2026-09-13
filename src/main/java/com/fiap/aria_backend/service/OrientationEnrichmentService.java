package com.fiap.aria_backend.service;

import com.fiap.aria_backend.model.*;
import com.fiap.aria_backend.repository.IdeaRepository;
import com.fiap.aria_backend.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
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

        double totalRoi = alignedProjects.stream()
                .mapToDouble(p -> p.getActualRoi() != null ? p.getActualRoi() : p.getEstimatedRoi())
                .sum();

        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        int ideasDelta = (int) alignedIdeas.stream()
                .filter(i -> i.getCreatedAt().isAfter(cutoff))
                .count();

        int projectsActive = (int) alignedProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.EM_ANDAMENTO)
                .count();

        int roiDeltaPercent = calculateRoiDeltaPercent(alignedProjects);

        return new EnrichedMetrics(
                alignedIdeas.size(),
                ideasDelta,
                projectsActive,
                formatCurrencyCompact(totalRoi),
                roiDeltaPercent,
                progress
        );
    }

    private int calculateRoiDeltaPercent(List<Project> alignedProjects) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusDays(30);

        double recent = alignedProjects.stream()
                .filter(p -> p.getStartDate().atStartOfDay().isAfter(cutoff))
                .mapToDouble(p -> p.getActualRoi() != null ? p.getActualRoi() : p.getEstimatedRoi())
                .sum();

        double older = alignedProjects.stream()
                .filter(p -> p.getStartDate().atStartOfDay().isBefore(cutoff))
                .mapToDouble(p -> p.getActualRoi() != null ? p.getActualRoi() : p.getEstimatedRoi())
                .sum();

        if (older <= 0) return recent <= 0 ? 0 : 100;
        return (int) Math.round(((recent - older) / older) * 100);
    }

    private String formatCurrencyCompact(double valueReais) {
        if (valueReais <= 0) return "R$ 0,00";
        double abs = Math.abs(valueReais);
        if (abs >= 1_000_000) {
            return "R$ " + new DecimalFormat("#,##0.#").format(abs / 1_000_000.0) + "M";
        } else if (abs >= 1_000) {
            return "R$ " + new DecimalFormat("#,##0.#").format(abs / 1_000.0) + "k";
        }
        return "R$ " + new DecimalFormat("#,##0.00").format(abs);
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