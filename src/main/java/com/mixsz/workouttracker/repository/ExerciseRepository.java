package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    Optional<Exercise> findByName(String name);
    List<Exercise> findByNameContainingIgnoreCase(String name);
    List<Exercise> findByMuscleGroup(MuscleGroup muscleGroup);

    @Query("SELECT e FROM Exercise e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND e.muscleGroup = :muscleGroup")
    List<Exercise> findByNameAndMuscleGroup(@Param("name") String name, @Param("muscleGroup") MuscleGroup muscleGroup);

}
