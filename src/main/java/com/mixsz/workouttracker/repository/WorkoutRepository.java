package com.mixsz.workouttracker.repository;

import com.mixsz.workouttracker.model.User;
import com.mixsz.workouttracker.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
    Optional<Workout> findByTitleAndUser(String title, User user);
    List<Workout> findByUser(User user);
    Optional<Workout> findByIdAndUser(UUID id, User user);
}
