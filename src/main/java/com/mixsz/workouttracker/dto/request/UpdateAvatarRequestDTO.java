package com.mixsz.workouttracker.dto.request;

import com.mixsz.workouttracker.enums.AvatarId;
import jakarta.validation.constraints.NotNull;

public record UpdateAvatarRequestDTO(@NotNull(message = "Avatar é obrigatório.") AvatarId avatarId) {
}