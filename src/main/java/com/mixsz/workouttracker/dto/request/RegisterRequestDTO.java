package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(@NotBlank(message = "Nome é obrigatório.")
                                 @Pattern(
                                         regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$",
                                         message = "O nome deve conter apenas letras."
                                 )
                                 String name,

                                 @NotBlank(message = "Email é obrigatório.")
                                 @Email(message = "Email inválido.")
                                 String email,

                                 @NotBlank(message = "Senha é obrigatória.")
                                 @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
                                 @Pattern(
                                         regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).*$",
                                         message = "A senha deve conter pelo menos um número, uma letra maiúscula e um caractere especial."
                                 )
                                 String password,

                                 String confirmPassword) {
}
