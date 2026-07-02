package com.mixsz.workouttracker.dto.request;

import com.mixsz.workouttracker.enums.MuscleGroup;

public record ExerciseRequestDTO(String name, MuscleGroup muscleGroup) {

}
