package com.mixsz.workouttracker.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkoutResponseDTO(UUID id, String title, int position, LocalDateTime lastTrainedAt) {
}
