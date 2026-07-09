package com.mixsz.workouttracker.service;

import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutLog;
import com.mixsz.workouttracker.repository.WorkoutLogRepository;
import com.mixsz.workouttracker.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutRepository workoutRepository;

    public WorkoutLogService(WorkoutLogRepository workoutLogRepository, WorkoutRepository workoutRepository) {
        this.workoutLogRepository = workoutLogRepository;
        this.workoutRepository = workoutRepository;
    }

    public List<WorkoutLog> findAll(User user){
        return workoutLogRepository.findByUserOrderByDateDesc(user);
    }

    public List<WorkoutLog> findByWorkout(UUID workoutId, User user){
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        return workoutLogRepository.findByWorkoutAndUserOrderByDateDesc(workout, user);
    }

    public List<WorkoutLog> findByDate(LocalDate date, User user) {
        return workoutLogRepository.findByUserAndDateBetweenOrderByDateDesc(
                user,
                date.atStartOfDay(),
                date.atTime(23, 59, 59));
    }

    public List<WorkoutLog> findByDateBetween(LocalDate start, LocalDate end, User user) {
        return workoutLogRepository.findByUserAndDateBetweenOrderByDateDesc(
                user,
                start.atStartOfDay(),
                end.atTime(23,59,59));
    }

    public List<WorkoutLog> findByDateBetweenAndWorkout(LocalDate start, LocalDate end, UUID workoutId, User user) {
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        return workoutLogRepository.findByWorkoutAndUserAndDateBetweenOrderByDateDesc(
                workout,
                user,
                start.atStartOfDay(),
                end.atTime(23,59,59));
    }

    @Transactional
    public WorkoutLog addWorkoutLog(UUID workoutId, User user) {
        Workout workout = workoutRepository.findByIdAndUser(workoutId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado!"));

        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setWorkout(workout);
        workoutLog.setUser(user);
        workoutLog.setDate(LocalDateTime.now());

        return workoutLogRepository.save(workoutLog);
    }

    @Transactional
    public void deleteWorkoutLog(UUID workoutLogId, User user){
        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUser(workoutLogId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de treino não encontrado!"));

        workoutLogRepository.delete(workoutLog);
    }
}