package com.mixsz.workouttracker.dto.response;

import com.mixsz.workouttracker.enums.UserRole;

import java.util.UUID;

public record UserResponseDTO(UUID id, String name, String email, UserRole role) {
}
