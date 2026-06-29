CREATE TABLE workout_log(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    date TIMESTAMP NOT NULL,
    user_id UUID NOT NULL,
    workout_id UUID NOT NULL,
    CONSTRAINT fk_workout_log_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_workout_log_workout FOREIGN KEY (workout_id) REFERENCES workout(id)
);