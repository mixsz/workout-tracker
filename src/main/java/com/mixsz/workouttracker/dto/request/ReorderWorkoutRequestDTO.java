package com.mixsz.workouttracker.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderWorkoutRequestDTO (@NotEmpty(message = "Os IDs dos treinos são obrigatórios.")
                                        List<UUID> workoutIds) {
}
