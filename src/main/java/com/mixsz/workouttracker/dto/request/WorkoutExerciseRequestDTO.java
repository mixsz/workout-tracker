package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record WorkoutExerciseRequestDTO(@NotNull(message = "Exercício é obrigatório.")
                                        UUID exerciseId,

                                        @Positive(message = "Número de séries inválido.")
                                        int sets,

                                        @Positive(message = "Número de repetições inválido.")
                                        int reps) {
}
