package com.mixsz.workouttracker.controller;

import com.mixsz.workouttracker.dto.request.UpdateAvatarRequestDTO;
import com.mixsz.workouttracker.dto.request.UpdateNameRequestDTO;
import com.mixsz.workouttracker.dto.request.UpdatePasswordRequestDTO;
import com.mixsz.workouttracker.dto.response.UserProfileResponseDTO;
import com.mixsz.workouttracker.dto.response.UserResponseDTO;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getProfile() {
        return ResponseEntity.ok(userService.getProfile(getCurrentUser()));
    }

    @PutMapping("/name")
    public ResponseEntity<UserResponseDTO> updateName(@RequestBody @Valid UpdateNameRequestDTO dto) {
        User updated = userService.updateName(getCurrentUser(), dto);
        return ResponseEntity.ok(new UserResponseDTO(updated.getId(), updated.getName(), updated.getEmail(),
                updated.getRole(), updated.getAvatarId()));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequestDTO dto) {
        userService.updatePassword(getCurrentUser(), dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/avatar")
    public ResponseEntity<UserResponseDTO> updateAvatar(@RequestBody @Valid UpdateAvatarRequestDTO dto) {
        User updated = userService.updateAvatar(getCurrentUser(), dto);
        return ResponseEntity.ok(new UserResponseDTO(updated.getId(), updated.getName(), updated.getEmail(),
                updated.getRole(), updated.getAvatarId()));
    }
}