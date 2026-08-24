ALTER TABLE users
    ADD COLUMN avatar_id VARCHAR(255) NOT NULL DEFAULT 'USER';

ALTER TABLE users
    ADD COLUMN created_at TIMESTAMP;

UPDATE users
    SET created_at = NOW()
    WHERE created_at IS NULL;

ALTER TABLE users
    ALTER COLUMN created_at SET NOT NULL;