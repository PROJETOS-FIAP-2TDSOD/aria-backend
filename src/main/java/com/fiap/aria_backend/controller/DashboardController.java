package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.DashboardEstrategiaRoiDto;
import com.fiap.aria_backend.dto.DashboardProjetoRoiDto;
import com.fiap.aria_backend.dto.DashboardResumoDto;
import com.fiap.aria_backend.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('LIDER')")
    public DashboardResumoDto summary() {
        return dashboardService.resumoGeral();
    }

    @GetMapping("/roi-by-strategy")
    @PreAuthorize("hasRole('LIDER')")
    public List<DashboardEstrategiaRoiDto> roiByStrategy() {
        return dashboardService.retornoPorEstrategia();
    }

    @GetMapping("/roi-by-project")
    @PreAuthorize("hasRole('LIDER')")
    public List<DashboardProjetoRoiDto> roiByProject() {
        return dashboardService.retornoPorProjeto();
    }
}