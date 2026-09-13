package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.ProjectRequestDto;
import com.fiap.aria_backend.dto.ProjectResponseDto;
import com.fiap.aria_backend.dto.ProjectUpdateProgressDto;
import com.fiap.aria_backend.security.AuthenticatedUser;
import com.fiap.aria_backend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final AuthenticatedUser authenticatedUser;

    public ProjectController(ProjectService projectService, AuthenticatedUser authenticatedUser) {
        this.projectService = projectService;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping
    public List<ProjectResponseDto> listAll() {
        return projectService.listAll();
    }

    @GetMapping("/{id}")
    public ProjectResponseDto findById(@PathVariable String id) {
        return projectService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GESTOR')")
    public ProjectResponseDto create(@Valid @RequestBody ProjectRequestDto request) {
        return projectService.create(request, authenticatedUser.getCurrentUserId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ProjectResponseDto update(@PathVariable String id, @Valid @RequestBody ProjectRequestDto request) {
        return projectService.update(id, request);
    }

    @PatchMapping("/{id}/progress")
    @PreAuthorize("hasRole('GESTOR')")
    public ProjectResponseDto updateProgress(@PathVariable String id, @RequestBody ProjectUpdateProgressDto dto) {
        return projectService.updateProgress(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        projectService.delete(id);
    }
}