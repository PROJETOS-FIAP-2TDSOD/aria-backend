package com.fiap.aria_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardProjetoRoiDto {
    private String projectId;
    private String titulo;
    private String status;
    private int progresso;
    private double investimento;
    private double roi;
    private double lucro;
    private String roiCompact;
    private String dataInicio;
    private String prazoFinal;
    private boolean atrasado;
}