package com.mixsz.workouttracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "workout_exercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int sets;

    private int reps;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private WorkoutModel workoutModel;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private ExerciseModel exerciseModel;
}
