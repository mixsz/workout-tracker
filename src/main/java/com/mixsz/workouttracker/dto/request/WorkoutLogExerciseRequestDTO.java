package com.mixsz.workouttracker.dto.request;

import java.util.UUID;

public record WorkoutLogExerciseRequestDTO(UUID exerciseId, int weightDone, int setsDone, int repsDone) {
}
