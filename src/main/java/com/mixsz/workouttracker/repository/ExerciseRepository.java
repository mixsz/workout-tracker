package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.model.ExerciseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseModel, UUID> {
    Optional<ExerciseModel> findByName(String name);
    List<ExerciseModel> findByNameContainingIgnoreCase(String name);
    List<ExerciseModel> findByMuscleGroup(MuscleGroup muscleGroup);

    @Query("SELECT e FROM ExerciseModel e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND e.muscleGroup = :muscleGroup")
    List<ExerciseModel> findByNameAndMuscleGroup(@Param("name") String name, @Param("muscleGroup") MuscleGroup muscleGroup);

}
