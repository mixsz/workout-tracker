package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.WorkoutLog;
import com.mixsz.workouttracker.model.WorkoutLogExercise;
import com.mixsz.workouttracker.repository.WorkoutLogExerciseRepository;
import com.mixsz.workouttracker.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutLogExerciseService {

    WorkoutLogExerciseRepository workoutLogExerciseRepository;
    WorkoutLogRepository workoutLogRepository;

    public WorkoutLogExerciseService(WorkoutLogExerciseRepository workoutLogExerciseRepository,
                                     WorkoutLogRepository workoutLogRepository) {
        this.workoutLogExerciseRepository = workoutLogExerciseRepository;
        this.workoutLogRepository = workoutLogRepository;
    }

    public List<WorkoutLogExercise> findAll(UUID workoutLogId, User user) {
        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUser(workoutLogId, user)
                .orElseThrow(() -> new RuntimeException("Registro de treino não encontrado!"));

        return workoutLogExerciseRepository.findByWorkoutLog(workoutLog);
    }

    public WorkoutLogExercise findById(UUID workoutLogId, UUID exerciseId, User user) {
        if (workoutLogRepository.findByIdAndUser(workoutLogId, user).isEmpty()) {
            throw new RuntimeException("Registro de treino não encontrado!");
        }

        return workoutLogExerciseRepository.findByWorkoutLogIdAndExerciseId(workoutLogId, exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado no registro de treino!"));
    }

}
