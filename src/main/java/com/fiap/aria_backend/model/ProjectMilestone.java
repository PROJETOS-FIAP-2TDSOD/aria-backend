package com.fiap.aria_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMilestone {
    private String id;
    private String title;
    private LocalDate dueDate;
    private MilestoneStatus status;
}