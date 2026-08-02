package com.mixsz.workouttracker.dto.response;

import com.mixsz.workouttracker.enums.MuscleGroup;

import java.util.UUID;

public record ExerciseResponseDTO(UUID id, String name, MuscleGroup muscleGroup, String muscleGroupLabel, String description) {

    public ExerciseResponseDTO(UUID id, String name, MuscleGroup muscleGroup, String description) {
        this(id, name, muscleGroup, muscleGroup.getDisplayName(), description);
    }
}