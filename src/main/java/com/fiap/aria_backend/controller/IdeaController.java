package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.IdeaRequestDto;
import com.fiap.aria_backend.dto.IdeaResponseDto;
import com.fiap.aria_backend.dto.IdeaReviewDto;
import com.fiap.aria_backend.service.IdeaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ideas")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @GetMapping
    public List<IdeaResponseDto> listAll() {
        return ideaService.listAll();
    }

    @GetMapping("/{id}")
    public IdeaResponseDto findById(@PathVariable String id) {
        return ideaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdeaResponseDto create(@Valid @RequestBody IdeaRequestDto request) {
        // TODO: trocar o id do usuário de teste pelo id do usuário autenticado via JWT
        return ideaService.create(request, "6aa6dc5ae213fa639420a704");
    }

    @PutMapping("/{id}")
    public IdeaResponseDto update(@PathVariable String id, @Valid @RequestBody IdeaRequestDto request) {
        return ideaService.update(id, request);
    }

    @PatchMapping("/{id}/review")
    public IdeaResponseDto review(@PathVariable String id, @RequestBody IdeaReviewDto reviewDto) {
        // TODO: restringir esse endpoint só a GESTOR quando JWT/roles estiverem prontos
        return ideaService.review(id, reviewDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        ideaService.delete(id);
    }
}