package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.NotificationMapper;
import com.fiap.aria_backend.dto.NotificationRequestDto;
import com.fiap.aria_backend.dto.NotificationResponseDto;
import com.fiap.aria_backend.model.Notification;
import com.fiap.aria_backend.model.NotificationType;
import com.fiap.aria_backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    public List<NotificationResponseDto> listByUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toResponseDto)
                .toList();
    }

    // Uso interno/sistema — ex: chamado por IdeaService quando um gestor aprova uma ideia
    public NotificationResponseDto create(NotificationRequestDto request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(NotificationType.valueOf(request.getType()))
                .isRead(false)
                .relatedIdeaId(request.getRelatedIdeaId())
                .createdAt(LocalDateTime.now())
                .build();

        return notificationMapper.toResponseDto(notificationRepository.save(notification));
    }

    public NotificationResponseDto markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada: " + id));
        notification.setRead(true);
        return notificationMapper.toResponseDto(notificationRepository.save(notification));
    }

    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public void delete(String id) {
        notificationRepository.deleteById(id);
    }
}