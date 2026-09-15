package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.AuthResponseDto;
import com.fiap.aria_backend.dto.LoginRequestDto;
import com.fiap.aria_backend.dto.MessageResponseDto;
import com.fiap.aria_backend.dto.RecoverPasswordRequestDto;
import com.fiap.aria_backend.dto.RegisterRequestDto;
import com.fiap.aria_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/recover-password")
    public MessageResponseDto recoverPassword(@Valid @RequestBody RecoverPasswordRequestDto request) {
        authService.recoverPassword(request);
        return MessageResponseDto.builder()
                .message("Se o e-mail informado estiver cadastrado, uma nova senha temporaria foi enviada.")
                .build();
    }
}