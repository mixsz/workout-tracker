package com.mixsz.workouttracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "workout_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogModel {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel userModel;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private WorkoutModel workoutModel;
}
