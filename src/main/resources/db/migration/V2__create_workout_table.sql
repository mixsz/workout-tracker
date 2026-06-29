CREATE TABLE workout(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(100) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_workout_user FOREIGN KEY (user_id) REFERENCES users(id)
);