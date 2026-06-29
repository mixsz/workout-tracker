CREATE TABLE exercise(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    muscle_group VARCHAR(100) NOT NULL
);