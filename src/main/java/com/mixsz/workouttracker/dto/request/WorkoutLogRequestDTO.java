package com.mixsz.workouttracker.dto.request;


import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WorkoutLogRequestDTO(@NotNull(message = "Treino é obrigatório")
                                   UUID workoutId) {
}
