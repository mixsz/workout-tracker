package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record WorkoutLogExerciseRequestDTO(@NotNull(message = "Exercício é obrigatório")
                                           UUID exerciseId,

                                           @PositiveOrZero(message = "Peso inválido.")
                                           int weightDone,

                                           @Positive(message = "Número de séries inválido.")
                                           int setsDone,

                                           @Positive(message = "Número de repetições inválido.")
                                           int repsDone) {
}
