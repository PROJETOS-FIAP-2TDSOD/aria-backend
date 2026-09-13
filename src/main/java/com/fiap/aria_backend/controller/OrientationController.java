package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.OrientationRequestDto;
import com.fiap.aria_backend.dto.OrientationResponseDto;
import com.fiap.aria_backend.security.AuthenticatedUser;
import com.fiap.aria_backend.service.OrientationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orientations")
public class OrientationController {

    private final OrientationService orientationService;
    private final AuthenticatedUser authenticatedUser;

    public OrientationController(OrientationService orientationService, AuthenticatedUser authenticatedUser) {
        this.orientationService = orientationService;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping
    public List<OrientationResponseDto> listAll() {
        return orientationService.listAll();
    }

    @GetMapping("/{id}")
    public OrientationResponseDto findById(@PathVariable String id) {
        return orientationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('LIDER')")
    public OrientationResponseDto create(@Valid @RequestBody OrientationRequestDto request) {
        return orientationService.create(request, authenticatedUser.getCurrentUserId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    public OrientationResponseDto update(@PathVariable String id, @Valid @RequestBody OrientationRequestDto request) {
        return orientationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        orientationService.delete(id);
    }
}