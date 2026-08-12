package com.mixsz.workouttracker.dto.response;

import java.util.UUID;

public record WorkoutLogExerciseResponseDTO(UUID id, ExerciseResponseDTO exercise, boolean done,
                                            Double weightDone, Integer setsDone, Integer repsDone, int position) {
}