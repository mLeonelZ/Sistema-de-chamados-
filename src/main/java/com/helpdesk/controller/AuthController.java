package com.helpdesk.controller;

import com.helpdesk.dto.auth.AuthLoginRequestDto;
import com.helpdesk.dto.auth.AuthLoginResponseDto;
import com.helpdesk.dto.auth.AuthRegisterRequestDto;
import com.helpdesk.dto.mapper.UserMapper;
import com.helpdesk.dto.user.UserResponseDto;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;
import com.helpdesk.service.JwtService;
import com.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponseDto> login(@RequestBody @Valid AuthLoginRequestDto dto) {
        User user = userService.authenticate(dto.email(), dto.password());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthLoginResponseDto(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid AuthRegisterRequestDto dto) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(Role.CLIENTE);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.toResponse(userService.save(user)));
    }
}
