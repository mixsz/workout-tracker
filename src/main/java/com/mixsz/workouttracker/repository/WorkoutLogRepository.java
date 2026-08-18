package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID>, JpaSpecificationExecutor<WorkoutLog> {
    List<WorkoutLog> findByWorkoutAndUserOrderByDateDesc(Workout workout, User user);
    Optional<WorkoutLog> findByIdAndUser(UUID id, User user);
    List<WorkoutLog> findByWorkoutAndUserAndDateBetweenOrderByDateDesc(Workout workout,
                                                                       User user,
                                                                       LocalDateTime start,
                                                                       LocalDateTime end);
    List<WorkoutLog> findByWorkout(Workout workout);
    Optional<WorkoutLog> findTopByWorkoutOrderByDateDesc(Workout workout);
    Optional<WorkoutLog> findByUserAndFinishedAtIsNull(User user);
    List<WorkoutLog> findByUserAndDateBetweenAndFinishedAtIsNotNullOrderByDateDesc(User user, LocalDateTime start, LocalDateTime end);
    List<WorkoutLog> findByUserAndFinishedAtIsNotNullOrderByDateDesc(User user);
}