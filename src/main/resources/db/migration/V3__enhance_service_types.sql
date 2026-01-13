-- V3: Add description, category, popular, and active columns to service_types table
-- This supports the enhanced service type data model for frontend UI

ALTER TABLE service_types ADD COLUMN description VARCHAR(500);
ALTER TABLE service_types ADD COLUMN category VARCHAR(100);
ALTER TABLE service_types ADD COLUMN popular BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE service_types ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;

-- Add unique constraint on name for MERGE operations
ALTER TABLE service_types ADD CONSTRAINT uk_service_types_name UNIQUE (name);

-- Create index on category for faster filtering
CREATE INDEX idx_service_types_category ON service_types(category);

-- Create index on active for filtering active services
CREATE INDEX idx_service_types_active ON service_types(active);
