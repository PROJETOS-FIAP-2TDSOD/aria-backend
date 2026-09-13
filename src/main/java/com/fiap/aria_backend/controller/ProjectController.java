package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.ProjectRequestDto;
import com.fiap.aria_backend.dto.ProjectResponseDto;
import com.fiap.aria_backend.dto.ProjectUpdateProgressDto;
import com.fiap.aria_backend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
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
    public ProjectResponseDto create(@Valid @RequestBody ProjectRequestDto request) {
        // TODO: trocar pelo id do gestor autenticado via JWT
        return projectService.create(request, "6aa6dc5ae213fa639420a704");
    }

    @PutMapping("/{id}")
    public ProjectResponseDto update(@PathVariable String id, @Valid @RequestBody ProjectRequestDto request) {
        return projectService.update(id, request);
    }

    @PatchMapping("/{id}/progress")
    public ProjectResponseDto updateProgress(@PathVariable String id, @RequestBody ProjectUpdateProgressDto dto) {
        // TODO: restringir só a GESTOR quando JWT/roles estiverem prontos
        return projectService.updateProgress(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        projectService.delete(id);
    }
}