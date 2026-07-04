package com.mixsz.workouttracker.dto.response;

import java.util.UUID;

public record WorkoutExerciseResponseDTO(UUID id, int sets, int reps, ExerciseResponseDTO exercise ) {
}
