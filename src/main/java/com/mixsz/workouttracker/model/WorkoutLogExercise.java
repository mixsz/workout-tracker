package com.mixsz.workouttracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "workout_log_exercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutLogExercise {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    private double weightDone;

    private int setsDone;

    private int repsDone;

    private int position;

    @ManyToOne
    @JoinColumn(name = "workout_log_id")
    private WorkoutLog workoutLog;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;
}
