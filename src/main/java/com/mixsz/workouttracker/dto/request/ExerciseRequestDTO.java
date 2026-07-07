package com.mixsz.workouttracker.dto.request;

import com.mixsz.workouttracker.enums.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciseRequestDTO(@NotBlank(message = "Nome de exercício obrigatório")
                                 String name,

                                 @NotNull (message = "Grupo muscular obrigatório")
                                 MuscleGroup muscleGroup) {

}
