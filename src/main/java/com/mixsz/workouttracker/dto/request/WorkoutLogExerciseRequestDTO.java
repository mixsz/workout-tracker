package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record WorkoutLogExerciseRequestDTO(@NotNull(message = "Exercício é obrigatório")
                                           UUID exerciseId,

                                           @PositiveOrZero(message = "Peso inválido.")
                                           @Digits(integer = 6, fraction = 2, message = "Peso precisa ter no máximo 2 casas decimais.")
                                           double weightDone,

                                           @Positive(message = "Número de séries inválido.")
                                           int setsDone,

                                           @Positive(message = "Número de repetições inválido.")

                                           int repsDone) {
}
