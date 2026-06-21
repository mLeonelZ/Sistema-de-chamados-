package com.helpdesk.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequestDto(
    @NotBlank(message = "Nome obrigatório!")
    String name,
    
    @NotBlank(message = "Email obrigatório!")
    @Email(message = "Formato de email inválido!")
    String email,
    
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    String password
) {
}
