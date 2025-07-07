-- Add guest column
ALTER TABLE customers
ADD COLUMN guest BOOLEAN NOT NULL DEFAULT FALSE;

-- Make email nullable for guest users
ALTER TABLE customers
MODIFY COLUMN email VARCHAR(255) NULL;
