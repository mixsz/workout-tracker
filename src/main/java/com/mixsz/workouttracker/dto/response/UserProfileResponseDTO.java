package com.mixsz.workouttracker.dto.response;

import com.mixsz.workouttracker.enums.AvatarId;
import com.mixsz.workouttracker.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponseDTO(
        UUID id,
        String name,
        String email,
        UserRole role,
        AvatarId avatarId,
        int totalWorkouts,
        int totalSessions,
        LocalDateTime createdAt
) {
}