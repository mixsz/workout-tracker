CREATE TABLE workout_log_exercise(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    weight_done INT NOT NULL,
    sets_done INT NOT NULL,
    reps_done INT NOT NULL,
    workout_log_id UUID NOT NULL,
    exercise_id UUID NOT NULL,
    CONSTRAINT fk_workout_log_exercise_workout_log FOREIGN KEY (workout_log_id) REFERENCES workout_log(id),
    CONSTRAINT fk_workout_log_exercise_exercise FOREIGN KEY (exercise_id) REFERENCES exercise(id)
);