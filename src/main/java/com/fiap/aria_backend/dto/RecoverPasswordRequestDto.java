package com.fiap.aria_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecoverPasswordRequestDto {

    @NotBlank
    @Email
    private String email;
}