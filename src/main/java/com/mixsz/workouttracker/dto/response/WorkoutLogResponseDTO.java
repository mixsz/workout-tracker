package com.mixsz.workouttracker.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkoutLogResponseDTO(UUID id, UUID workoutId, String workoutTitle, LocalDateTime date) {

}
