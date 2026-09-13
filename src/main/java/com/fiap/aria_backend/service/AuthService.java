package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.AuthResponseDto;
import com.fiap.aria_backend.dto.LoginRequestDto;
import com.fiap.aria_backend.dto.RegisterRequestDto;
import com.fiap.aria_backend.dto.UserSummaryDto;
import com.fiap.aria_backend.model.User;
import com.fiap.aria_backend.model.UserRole;
import com.fiap.aria_backend.repository.UserRepository;
import com.fiap.aria_backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com esse e-mail");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.valueOf(request.getRole()))
                .department("")
                .points(0)
                .badges(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));

        return buildAuthResponse(user);
    }

    private AuthResponseDto buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        UserSummaryDto userSummary = UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .avatarInitials(user.getAvatarInitials())
                .totalIdeas(0)
                .approvedIdeas(0)
                .build();

        return AuthResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInSeconds(jwtService.getExpirationSeconds())
                .user(userSummary)
                .build();
    }
}