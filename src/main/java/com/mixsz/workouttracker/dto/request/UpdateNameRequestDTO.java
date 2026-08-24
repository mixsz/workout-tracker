package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateNameRequestDTO(
        @NotBlank(message = "Nome é obrigatório.")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$",
                message = "O nome deve conter apenas letras."
        )
        String name
) {
}