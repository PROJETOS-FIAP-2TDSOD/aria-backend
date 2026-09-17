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
public class UserSummaryDto {
    private String id;
    private String name;
    private String email;
    private String role;
    private String department;
    private String avatarInitials;
    private int totalIdeas;
    private int approvedIdeas;
    private int points;
    private List<String> badges;
}