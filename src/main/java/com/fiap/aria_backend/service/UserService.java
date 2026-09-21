package com.fiap.aria_backend.service;

import com.fiap.aria_backend.dto.IdeaMapper;
import com.fiap.aria_backend.dto.UserSummaryDto;
import com.fiap.aria_backend.model.User;
import com.fiap.aria_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final IdeaMapper ideaMapper;

    public UserService(UserRepository userRepository, IdeaMapper ideaMapper) {
        this.userRepository = userRepository;
        this.ideaMapper = ideaMapper;
    }

    public UserSummaryDto getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        return ideaMapper.toUserSummary(user);
    }

    public List<UserSummaryDto> listAll() {
        return userRepository.findAll().stream()
                .map(ideaMapper::toUserSummary)
                .toList();
    }
}