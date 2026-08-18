package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.model.WorkoutLog;
import com.mixsz.workouttracker.model.WorkoutLogExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutLogExerciseRepository extends JpaRepository<WorkoutLogExercise, UUID> {
    List<WorkoutLogExercise> findByWorkoutLogOrderByPositionAsc(WorkoutLog workoutLog);
    Optional<WorkoutLogExercise> findByWorkoutLogIdAndExerciseId(UUID workoutLogId, UUID exerciseId);

    @Query("SELECT wle.workoutLog.id AS workoutLogId, " +
            "COUNT(wle) AS planned, " +
            "SUM(CASE WHEN wle.done = true THEN 1 ELSE 0 END) AS done " +
            "FROM WorkoutLogExercise wle " +
            "WHERE wle.workoutLog.id IN :workoutLogIds " +
            "GROUP BY wle.workoutLog.id")
    List<WorkoutLogExerciseCountProjection> countByWorkoutLogIds(@Param("workoutLogIds") List<UUID> workoutLogIds);

    interface WorkoutLogExerciseCountProjection {
        UUID getWorkoutLogId();
        Long getPlanned();
        Long getDone();
    }
}
