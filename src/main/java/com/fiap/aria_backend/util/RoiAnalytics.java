package com.fiap.aria_backend.util;

import com.fiap.aria_backend.model.Project;

import java.time.LocalDateTime;
import java.util.List;

public final class RoiAnalytics {

    private RoiAnalytics() {
    }

    public static double roiOf(Project project) {
        return project.getActualRoi() != null ? project.getActualRoi() : project.getEstimatedRoi();
    }

    public static double totalRoi(List<Project> projects) {
        return projects.stream().mapToDouble(RoiAnalytics::roiOf).sum();
    }

    // Compara ROI dos projetos iniciados nos últimos 30 dias vs. antes disso
    public static int deltaPercentLast30Days(List<Project> projects) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        double recent = projects.stream()
                .filter(p -> p.getStartDate().atStartOfDay().isAfter(cutoff))
                .mapToDouble(RoiAnalytics::roiOf)
                .sum();

        double older = projects.stream()
                .filter(p -> p.getStartDate().atStartOfDay().isBefore(cutoff))
                .mapToDouble(RoiAnalytics::roiOf)
                .sum();

        if (older <= 0) return recent <= 0 ? 0 : 100;
        return (int) Math.round(((recent - older) / older) * 100);
    }
}