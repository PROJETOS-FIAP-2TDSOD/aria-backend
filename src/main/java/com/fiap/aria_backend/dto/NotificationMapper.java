package com.fiap.aria_backend.dto;

import com.fiap.aria_backend.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDto toResponseDto(Notification n) {
        return NotificationResponseDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType().name())
                .isRead(n.isRead())
                .relatedIdeaId(n.getRelatedIdeaId())
                .createdAt(n.getCreatedAt().toString())
                .build();
    }
}