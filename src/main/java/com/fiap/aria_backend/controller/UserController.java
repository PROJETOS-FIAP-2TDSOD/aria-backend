package com.fiap.aria_backend.controller;

import com.fiap.aria_backend.dto.UserSummaryDto;
import com.fiap.aria_backend.security.AuthenticatedUser;
import com.fiap.aria_backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;

    public UserController(UserService userService, AuthenticatedUser authenticatedUser) {
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping("/me")
    public UserSummaryDto me() {
        return userService.getById(authenticatedUser.getCurrentUserId());
    }

    @GetMapping
    public List<UserSummaryDto> listAll() {
        return userService.listAll();
    }

    @GetMapping("/{id}")
    public UserSummaryDto getById(@PathVariable String id) {
        return userService.getById(id);
    }
}