package com.fiap.aria_backend.dto;

import lombok.Data;

@Data
public class ProjectUpdateProgressDto {
    private String status;
    private Integer progress;
    private Double actualRoi;
}