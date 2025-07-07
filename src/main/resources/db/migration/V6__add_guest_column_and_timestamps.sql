-- Add guest column
ALTER TABLE customers
ADD COLUMN guest BOOLEAN NOT NULL DEFAULT FALSE;

-- Make email nullable for guest users (H2 compatible syntax)
ALTER TABLE customers
ALTER COLUMN email VARCHAR(255) NULL;
