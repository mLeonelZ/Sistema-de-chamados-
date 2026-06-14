package com.helpdesk.dto.user;

import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserRequestDto(
        @NotBlank(message = "Nome obrigatório!")
        String name,
        @NotBlank(message = "Email obrigatório!")
        @Email(message = "Formato de email inválido!")
        String email,
        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        String password,
        @NotNull(message = "O cargo (Role) é obrigatório.")
        Role role,
        @NotNull(message = "O status do usuário é obrigatório.")
        UserStatus status,
        @NotNull(message = "O ID do departamento é obrigatório.")
        UUID departmentId
) {
}
