package com.mixsz.workouttracker.dto.response;

import java.util.UUID;

public record WorkoutLogExerciseResponseDTO(UUID id, ExerciseResponseDTO exercise, double weightDone, int setsDone,
                                            int repsDone, int position) {
}
