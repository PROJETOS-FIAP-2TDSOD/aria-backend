package com.fiap.aria_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String role = "OPERADOR";
}