ALTER TABLE workout_log ADD COLUMN finished_at TIMESTAMP NULL;
UPDATE workout_log SET finished_at = date WHERE finished_at IS NULL;