package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WorkoutRequestDTO(@NotBlank(message = "Nome do treino é obrigatório.")
                                String title) {
}
