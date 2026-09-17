package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.AuthResponseDto;
import com.fiap.aria_backend.dto.IdeaMapper;
import com.fiap.aria_backend.dto.LoginRequestDto;
import com.fiap.aria_backend.dto.RecoverPasswordRequestDto;
import com.fiap.aria_backend.dto.RegisterRequestDto;
import com.fiap.aria_backend.model.User;
import com.fiap.aria_backend.model.UserRole;
import com.fiap.aria_backend.repository.UserRepository;
import com.fiap.aria_backend.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
public class AuthService {

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_LENGTH = 10;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IdeaMapper ideaMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService, IdeaMapper ideaMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.ideaMapper = ideaMapper;
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

    // Nao revela se o e-mail existe ou nao: resposta do controller e sempre a mesma.
    public void recoverPassword(RecoverPasswordRequestDto request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String tempPassword = generateTempPassword();
            user.setPasswordHash(passwordEncoder.encode(tempPassword));
            userRepository.save(user);

            log.info("[recover-password] Senha temporaria gerada para {}: {}", user.getEmail(), tempPassword);
        });
    }

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            sb.append(TEMP_PASSWORD_CHARS.charAt(secureRandom.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    private AuthResponseDto buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInSeconds(jwtService.getExpirationSeconds())
                .user(ideaMapper.toUserSummary(user))
                .build();
    }
}