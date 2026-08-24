package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequestDTO(
        @NotBlank(message = "Senha atual é obrigatória.")
        String currentPassword,

        @NotBlank(message = "Nova senha é obrigatória.")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).*$",
                message = "A senha deve conter pelo menos um número, uma letra maiúscula e um caractere especial."
        )
        String newPassword,

        @NotBlank(message = "Confirmação é obrigatória.")
        String confirmNewPassword
) {
}