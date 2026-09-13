package com.fiap.aria_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private String id;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private String relatedIdeaId;
    private String createdAt;
}