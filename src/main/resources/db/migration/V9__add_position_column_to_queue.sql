-- Add position column to queue table
ALTER TABLE queue ADD COLUMN position INT NOT NULL DEFAULT 1;
