package com.fiap.aria_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorEstrategicoResumoDto {
    private String orientationId;
    private String orientationTitle;
    private String name;
    private String achieved;
    private String target;
    private float progress;
}