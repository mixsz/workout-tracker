package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderWorkoutExerciseRequestDTO(@NotEmpty(message = "Os IDs dos exercícios não podem estar vazios!")
                                               List<UUID> exerciseIds) {
}
