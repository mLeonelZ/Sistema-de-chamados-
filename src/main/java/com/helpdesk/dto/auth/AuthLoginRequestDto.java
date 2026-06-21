package com.helpdesk.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDto(
        @NotBlank(message = "Email obrigatório!")
        @Email(message = "Formato de email inválido!")
        String email,
        @NotBlank(message = "Senha obrigatória!")
        String password
) {
}
