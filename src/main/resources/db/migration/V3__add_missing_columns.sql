-- Add missing columns to vehicle_smartcar_mapping table
-- This migration adds the missing columns that the VehicleSmartcarMapping entity expects

-- Add is_active column
ALTER TABLE vehicle_smartcar_mapping 
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true;

-- Add last_location_fetch column
ALTER TABLE vehicle_smartcar_mapping 
ADD COLUMN last_location_fetch TIMESTAMP;

-- Add last_location_latitude column
ALTER TABLE vehicle_smartcar_mapping 
ADD COLUMN last_location_latitude DOUBLE PRECISION;

-- Add last_location_longitude column
ALTER TABLE vehicle_smartcar_mapping 
ADD COLUMN last_location_longitude DOUBLE PRECISION;

-- Add comments for the new columns
COMMENT ON COLUMN vehicle_smartcar_mapping.is_active IS 'Whether this mapping is active and can be used for location fetching';
COMMENT ON COLUMN vehicle_smartcar_mapping.last_location_fetch IS 'Timestamp of the last successful location fetch';
COMMENT ON COLUMN vehicle_smartcar_mapping.last_location_latitude IS 'Latitude from the last location fetch';
COMMENT ON COLUMN vehicle_smartcar_mapping.last_location_longitude IS 'Longitude from the last location fetch';

-- Update existing records to have is_active = true
UPDATE vehicle_smartcar_mapping SET is_active = true WHERE is_active IS NULL;
