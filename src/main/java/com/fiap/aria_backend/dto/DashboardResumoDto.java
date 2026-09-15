package com.fiap.aria_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumoDto {

    private int ideasSubmetidas;
    private int ideasAprovadas;
    private int ideasEmAnalise;
    private int ideasEmProjeto;
    private int taxaAprovacaoPercent;
    private int taxaConversaoPercent;

    private int projetosPlanejamento;
    private int projetosEmAndamento;
    private int projetosConcluidos;
    private int projetosSuspensos;
    private int projetosCancelados;
    private int projetosNoPrazo;
    private int projetosAtrasados;

    private double investimentoTotal;
    private double roiTotal;
    private double lucroTotal;
    private String investimentoTotalCompact;
    private String roiTotalCompact;
    private String lucroTotalCompact;
    private int roiDeltaPercent30d;

    private List<IndicadorEstrategicoResumoDto> indicadoresEstrategicos;
}