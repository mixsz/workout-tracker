package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.model.Workout;
import com.mixsz.workouttracker.model.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository <WorkoutExercise, UUID> {
    List<WorkoutExercise> findByWorkoutOrderByPositionAsc(Workout workout);
    Optional<WorkoutExercise> findByWorkoutIdAndExerciseId(UUID workoutId, UUID exerciseId);
    int countByWorkout(Workout workout);
    List<WorkoutExercise> findByWorkoutAndPositionGreaterThan(Workout workout, int position);
}
