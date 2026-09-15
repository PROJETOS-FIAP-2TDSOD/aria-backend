package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.*;
import com.fiap.aria_backend.model.*;
import com.fiap.aria_backend.repository.IdeaRepository;
import com.fiap.aria_backend.repository.OrientationRepository;
import com.fiap.aria_backend.repository.ProjectRepository;
import com.fiap.aria_backend.util.CurrencyFormatter;
import com.fiap.aria_backend.util.RoiAnalytics;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final IdeaRepository ideaRepository;
    private final ProjectRepository projectRepository;
    private final OrientationRepository orientationRepository;
    private final OrientationEnrichmentService enrichmentService;

    public DashboardService(IdeaRepository ideaRepository, ProjectRepository projectRepository,
                            OrientationRepository orientationRepository, OrientationEnrichmentService enrichmentService) {
        this.ideaRepository = ideaRepository;
        this.projectRepository = projectRepository;
        this.orientationRepository = orientationRepository;
        this.enrichmentService = enrichmentService;
    }

    public DashboardResumoDto resumoGeral() {
        List<Idea> ideas = ideaRepository.findAll();
        List<Project> projects = projectRepository.findAll();
        List<Orientation> orientations = orientationRepository.findAll();

        int submitted = ideas.size();
        int approved = (int) ideas.stream()
                .filter(i -> i.getStatus() == IdeaStatus.APROVADA || i.getStatus() == IdeaStatus.EM_PROJETO)
                .count();
        int emAnalise = (int) ideas.stream()
                .filter(i -> i.getStatus() == IdeaStatus.EM_ANALISE || i.getStatus() == IdeaStatus.AGUARDANDO_ANALISE)
                .count();
        int emProjeto = (int) ideas.stream().filter(i -> i.getStatus() == IdeaStatus.EM_PROJETO).count();

        Map<ProjectStatus, Long> porStatus = projects.stream()
                .collect(Collectors.groupingBy(Project::getStatus, Collectors.counting()));

        LocalDate hoje = LocalDate.now();
        int noPrazo = (int) projects.stream()
                .filter(p -> p.getStatus() != ProjectStatus.CONCLUIDO && p.getStatus() != ProjectStatus.CANCELADO)
                .filter(p -> !p.getExpectedEndDate().isBefore(hoje))
                .count();
        int atrasados = (int) projects.stream()
                .filter(p -> p.getStatus() != ProjectStatus.CONCLUIDO && p.getStatus() != ProjectStatus.CANCELADO)
                .filter(p -> p.getExpectedEndDate().isBefore(hoje))
                .count();

        double investimentoTotal = projects.stream().mapToDouble(Project::getBudget).sum();
        double roiTotal = RoiAnalytics.totalRoi(projects);
        double lucroTotal = roiTotal - investimentoTotal;

        List<IndicadorEstrategicoResumoDto> indicadores = orientations.stream()
                .flatMap(o -> o.getKeyMetrics().stream().map(m -> IndicadorEstrategicoResumoDto.builder()
                        .orientationId(o.getId())
                        .orientationTitle(o.getTitle())
                        .name(m.getName())
                        .achieved(m.getAchieved())
                        .target(m.getTarget())
                        .progress(m.getProgress())
                        .build()))
                .sorted(Comparator.comparing(IndicadorEstrategicoResumoDto::getProgress))
                .toList();

        return DashboardResumoDto.builder()
                .ideasSubmetidas(submitted)
                .ideasAprovadas(approved)
                .ideasEmAnalise(emAnalise)
                .ideasEmProjeto(emProjeto)
                .taxaAprovacaoPercent(submitted == 0 ? 0 : (approved * 100) / submitted)
                .taxaConversaoPercent(submitted == 0 ? 0 : (emProjeto * 100) / submitted)
                .projetosPlanejamento(porStatus.getOrDefault(ProjectStatus.PLANEJAMENTO, 0L).intValue())
                .projetosEmAndamento(porStatus.getOrDefault(ProjectStatus.EM_ANDAMENTO, 0L).intValue())
                .projetosConcluidos(porStatus.getOrDefault(ProjectStatus.CONCLUIDO, 0L).intValue())
                .projetosSuspensos(porStatus.getOrDefault(ProjectStatus.SUSPENSO, 0L).intValue())
                .projetosCancelados(porStatus.getOrDefault(ProjectStatus.CANCELADO, 0L).intValue())
                .projetosNoPrazo(noPrazo)
                .projetosAtrasados(atrasados)
                .investimentoTotal(investimentoTotal)
                .roiTotal(roiTotal)
                .lucroTotal(lucroTotal)
                .investimentoTotalCompact(CurrencyFormatter.toCompactReais(investimentoTotal))
                .roiTotalCompact(CurrencyFormatter.toCompactReais(roiTotal))
                .lucroTotalCompact(CurrencyFormatter.toCompactReais(lucroTotal))
                .roiDeltaPercent30d(RoiAnalytics.deltaPercentLast30Days(projects))
                .indicadoresEstrategicos(indicadores)
                .build();
    }

    public List<DashboardEstrategiaRoiDto> retornoPorEstrategia() {
        return orientationRepository.findAll().stream()
                .map(o -> {
                    var metrics = enrichmentService.enrich(o);
                    return DashboardEstrategiaRoiDto.builder()
                            .orientationId(o.getId())
                            .titulo(o.getTitle())
                            .categoria(o.getCategory().name())
                            .ideasCount(metrics.ideasCount())
                            .projetosAtivos(metrics.projectsActive())
                            .roiCompact(metrics.roiCompact())
                            .roiDeltaPercent(metrics.roiDeltaPercent())
                            .progresso(metrics.progress())
                            .build();
                })
                .toList();
    }

    public List<DashboardProjetoRoiDto> retornoPorProjeto() {
        LocalDate hoje = LocalDate.now();
        return projectRepository.findAll().stream()
                .map(p -> {
                    double roi = RoiAnalytics.roiOf(p);
                    boolean atrasado = p.getStatus() != ProjectStatus.CONCLUIDO
                            && p.getStatus() != ProjectStatus.CANCELADO
                            && p.getExpectedEndDate().isBefore(hoje);
                    return DashboardProjetoRoiDto.builder()
                            .projectId(p.getId())
                            .titulo(p.getTitle())
                            .status(p.getStatus().name())
                            .progresso(p.getProgress())
                            .investimento(p.getBudget())
                            .roi(roi)
                            .lucro(roi - p.getBudget())
                            .roiCompact(CurrencyFormatter.toCompactReais(roi))
                            .dataInicio(p.getStartDate().toString())
                            .prazoFinal(p.getExpectedEndDate().toString())
                            .atrasado(atrasado)
                            .build();
                })
                .sorted(Comparator.comparingDouble(DashboardProjetoRoiDto::getRoi).reversed())
                .toList();
    }
}