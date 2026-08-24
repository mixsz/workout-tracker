package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.dto.request.UpdateAvatarRequestDTO;
import com.mixsz.workouttracker.dto.request.UpdateNameRequestDTO;
import com.mixsz.workouttracker.dto.request.UpdatePasswordRequestDTO;
import com.mixsz.workouttracker.dto.response.UserProfileResponseDTO;
import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.repository.UserRepository;
import com.mixsz.workouttracker.repository.WorkoutLogRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, WorkoutRepository workoutRepository,
                       WorkoutLogRepository workoutLogRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponseDTO getProfile(User user) {
        int totalWorkouts = workoutRepository.countByUser(user);
        int totalSessions = workoutLogRepository.countByUserAndFinishedAtIsNotNull(user);

        return new UserProfileResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getAvatarId(),
                totalWorkouts,
                totalSessions,
                user.getCreatedAt()
        );
    }

    @Transactional
    public User updateName(User user, UpdateNameRequestDTO dto) {
        String newName = dto.name().trim();
        if (newName.equalsIgnoreCase(user.getName())) {
            throw new BusinessException("O novo nome deve ser diferente do atual!");
        }
        user.setName(newName);
        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(User user, UpdatePasswordRequestDTO dto) {
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessException("Senha atual incorreta!");
        }
        if (!dto.newPassword().equals(dto.confirmNewPassword())) {
            throw new BusinessException("As senhas não coincidem!");
        }
        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new BusinessException("A nova senha deve ser diferente da atual!");
        }
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public User updateAvatar(User user, UpdateAvatarRequestDTO dto) {
        user.setAvatarId(dto.avatarId());
        return userRepository.save(user);
    }
}