package com.mixsz.workouttracker.dto.response;

import com.mixsz.workouttracker.enums.MuscleGroup;

import java.util.UUID;

public record  ExerciseResponseDTO(UUID id, String name, MuscleGroup muscleGroup) {
}
