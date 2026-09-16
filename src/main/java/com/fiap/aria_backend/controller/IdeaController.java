package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.IdeaRequestDto;
import com.fiap.aria_backend.dto.IdeaResponseDto;
import com.fiap.aria_backend.dto.IdeaReviewDto;
import com.fiap.aria_backend.security.AuthenticatedUser;
import com.fiap.aria_backend.service.IdeaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ideas")
public class IdeaController {

    private final IdeaService ideaService;
    private final AuthenticatedUser authenticatedUser;

    public IdeaController(IdeaService ideaService, AuthenticatedUser authenticatedUser) {
        this.ideaService = ideaService;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping
    public List<IdeaResponseDto> listAll() {
        if ("OPERADOR".equals(authenticatedUser.getCurrentUserRole())) {
            return ideaService.listByAuthor(authenticatedUser.getCurrentUserId());
        }
        return ideaService.listAll();
    }

    @GetMapping("/{id}")
    public IdeaResponseDto findById(@PathVariable String id) {
        return ideaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdeaResponseDto create(@Valid @RequestBody IdeaRequestDto request) {
        return ideaService.create(request, authenticatedUser.getCurrentUserId());
    }

    @PutMapping("/{id}")
    public IdeaResponseDto update(@PathVariable String id, @Valid @RequestBody IdeaRequestDto request) {
        return ideaService.update(id, request, authenticatedUser.getCurrentUserId());
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasRole('GESTOR')")
    public IdeaResponseDto review(@PathVariable String id, @RequestBody IdeaReviewDto reviewDto) {
        return ideaService.review(id, reviewDto);
    }

    @PostMapping("/{id}/ai-score")
    @PreAuthorize("hasRole('GESTOR')")
    public IdeaResponseDto scoreWithAi(@PathVariable String id) {
        return ideaService.scoreWithAi(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        ideaService.delete(id, authenticatedUser.getCurrentUserId());
    }
}