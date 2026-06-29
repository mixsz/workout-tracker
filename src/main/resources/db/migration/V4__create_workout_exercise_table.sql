CREATE TABLE workout_exercise(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sets INT NOT NULL,
    reps INT NOT NULL,
    workout_id UUID NOT NULL,
    exercise_id UUID NOT NULL,
    CONSTRAINT fk_workout_exercise_workout FOREIGN KEY (workout_id) REFERENCES workout(id),
    CONSTRAINT fk_workout_exercise_exercise FOREIGN KEY (exercise_id) REFERENCES exercise(id)
);