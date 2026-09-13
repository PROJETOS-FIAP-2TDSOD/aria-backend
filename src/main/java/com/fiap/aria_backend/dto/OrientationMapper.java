package com.fiap.aria_backend.dto;

import com.fiap.aria_backend.model.Orientation;
import com.fiap.aria_backend.model.OrientationKeyMetric;
import com.fiap.aria_backend.model.User;
import com.fiap.aria_backend.service.OrientationEnrichmentService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrientationMapper {

    public OrientationResponseDto toResponseDto(Orientation o, User author, OrientationEnrichmentService.EnrichedMetrics metrics) {
        return OrientationResponseDto.builder()
                .id(o.getId())
                .code(o.getCode())
                .title(o.getTitle())
                .description(o.getDescription())
                .author(toUserSummary(author))
                .category(o.getCategory().name())
                .priority(o.getPriority().name())
                .period(o.getPeriod())
                .targetRoles(o.getTargetRoles().stream().map(Enum::name).toList())
                .keyMetrics(mapKeyMetrics(o.getKeyMetrics()))
                .ideasCount(metrics.ideasCount())
                .ideasDelta(metrics.ideasDelta())
                .projectsActive(metrics.projectsActive())
                .roiCompact(metrics.roiCompact())
                .roiDeltaPercent(metrics.roiDeltaPercent())
                .progress(metrics.progress())
                .createdAt(o.getCreatedAt().toString())
                .expiresAt(o.getExpiresAt() != null ? o.getExpiresAt().toString() : null)
                .build();
    }

    private List<OrientationKeyMetricDto> mapKeyMetrics(List<OrientationKeyMetric> metrics) {
        return metrics.stream()
                .map(m -> OrientationKeyMetricDto.builder()
                        .name(m.getName())
                        .achieved(m.getAchieved())
                        .target(m.getTarget())
                        .progress(m.getProgress())
                        .build())
                .toList();
    }

    private UserSummaryDto toUserSummary(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .avatarInitials(user.getAvatarInitials())
                .totalIdeas(0)
                .approvedIdeas(0)
                .build();
    }
}