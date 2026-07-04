package com.mixsz.workouttracker.dto.request;

import java.util.UUID;

public record WorkoutExerciseRequestDTO(UUID exerciseId, int sets, int reps) {
}
