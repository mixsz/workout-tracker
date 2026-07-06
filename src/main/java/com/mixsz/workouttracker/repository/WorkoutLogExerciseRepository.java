package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.model.WorkoutLog;
import com.mixsz.workouttracker.model.WorkoutLogExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutLogExerciseRepository extends JpaRepository <WorkoutLogExercise, UUID> {
    List<WorkoutLogExercise> findByWorkoutLog(WorkoutLog workoutLog);
    Optional<WorkoutLogExercise> findByWorkoutLogIdAndExerciseId(UUID workoutLogId, UUID exerciseId);
}
