DELETE FROM workout_log_exercise;
DELETE FROM workout_exercise;
DELETE FROM exercise;

ALTER TABLE exercise ADD COLUMN description VARCHAR(255) NOT NULL;