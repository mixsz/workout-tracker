package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID> {
    List<WorkoutLog> findByWorkoutAndUserOrderByDateDesc(Workout workout, User user);
    Optional<WorkoutLog> findByIdAndUser(UUID id, User user);
    List<WorkoutLog> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDateTime start, LocalDateTime end);
    List<WorkoutLog> findByUserOrderByDateDesc(User user);
    List<WorkoutLog> findByWorkoutAndUserAndDateBetweenOrderByDateDesc(Workout workout,
                                                                       User user,
                                                                       LocalDateTime start,
                                                                       LocalDateTime end);
}
