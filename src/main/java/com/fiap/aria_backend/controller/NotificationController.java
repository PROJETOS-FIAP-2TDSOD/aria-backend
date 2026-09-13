package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.NotificationRequestDto;
import com.fiap.aria_backend.dto.NotificationResponseDto;
import com.fiap.aria_backend.security.AuthenticatedUser;
import com.fiap.aria_backend.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthenticatedUser authenticatedUser;

    public NotificationController(NotificationService notificationService, AuthenticatedUser authenticatedUser) {
        this.notificationService = notificationService;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping
    public List<NotificationResponseDto> listMyNotifications() {
        return notificationService.listByUser(authenticatedUser.getCurrentUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponseDto create(@Valid @RequestBody NotificationRequestDto request) {
        return notificationService.create(request);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponseDto markAsRead(@PathVariable String id) {
        return notificationService.markAsRead(id);
    }

    @PatchMapping("/read-all")
    public void markAllAsRead() {
        notificationService.markAllAsRead(authenticatedUser.getCurrentUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        notificationService.delete(id);
    }
}