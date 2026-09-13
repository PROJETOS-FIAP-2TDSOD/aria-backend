package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.OrientationRequestDto;
import com.fiap.aria_backend.dto.OrientationResponseDto;
import com.fiap.aria_backend.service.OrientationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orientations")
public class OrientationController {

    private final OrientationService orientationService;

    public OrientationController(OrientationService orientationService) {
        this.orientationService = orientationService;
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
    public OrientationResponseDto create(@Valid @RequestBody OrientationRequestDto request) {
        // TODO: trocar pelo id do líder autenticado via JWT; restringir esse endpoint a LIDER
        return orientationService.create(request, "6aa6dc5ae213fa639420a704");
    }

    @PutMapping("/{id}")
    public OrientationResponseDto update(@PathVariable String id, @Valid @RequestBody OrientationRequestDto request) {
        // TODO: restringir a LIDER quando JWT/roles estiverem prontos
        return orientationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        // TODO: restringir a LIDER quando JWT/roles estiverem prontos
        orientationService.delete(id);
    }
}