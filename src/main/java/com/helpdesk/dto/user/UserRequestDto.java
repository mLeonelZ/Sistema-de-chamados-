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
        String password,
        @NotNull(message = "O cargo (Role) é obrigatório.")
        Role role,
        @NotNull(message = "O status do usuário é obrigatório.")
        UserStatus status,
        UUID departmentId
) {
}
