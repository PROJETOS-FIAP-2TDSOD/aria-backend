package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.NotificationRequestDto;
import com.fiap.aria_backend.dto.NotificationResponseDto;
import com.fiap.aria_backend.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // TODO: userId deve vir do usuário autenticado (JWT), não de query param —
    // manter como query param só até a branch de segurança existir.
    @GetMapping
    public List<NotificationResponseDto> listByUser(@RequestParam String userId) {
        return notificationService.listByUser(userId);
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
    public void markAllAsRead(@RequestParam String userId) {
        notificationService.markAllAsRead(userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        notificationService.delete(id);
    }
}