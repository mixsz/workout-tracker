package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.enums.MuscleGroup;
import com.mixsz.workouttracker.model.Exercise;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID>, JpaSpecificationExecutor<Exercise> {
    Optional<Exercise> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    List<Exercise> findByNameContainingIgnoreCase(String name, Sort sort);
    List<Exercise> findByMuscleGroup(MuscleGroup muscleGroup, Sort sort);

    @Query("SELECT e FROM Exercise e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND e.muscleGroup = :muscleGroup")
    List<Exercise> findByNameAndMuscleGroup(@Param("name") String name, @Param("muscleGroup") MuscleGroup muscleGroup, Sort sort);

}