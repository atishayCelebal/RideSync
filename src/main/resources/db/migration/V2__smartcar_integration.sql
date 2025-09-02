-- Smartcar Integration Tables
-- This migration adds support for Smartcar OAuth tokens and vehicle ID mapping

-- Table to store Smartcar OAuth tokens for users
CREATE TABLE user_smartcar_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    access_token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500),
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_user_smartcar_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_user_smartcar_tokens_user UNIQUE (user_id)
);

-- Table to map internal vehicles to Smartcar vehicle IDs
CREATE TABLE vehicle_smartcar_mapping (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID NOT NULL,
    smartcar_vehicle_id VARCHAR(100) NOT NULL,
    user_id UUID NOT NULL,
    vehicle_info JSONB, -- Store make, model, year from Smartcar
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_vehicle_smartcar_mapping_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE CASCADE,
    CONSTRAINT fk_vehicle_smartcar_mapping_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_vehicle_smartcar_mapping_vehicle UNIQUE (vehicle_id),
    CONSTRAINT uk_vehicle_smartcar_mapping_smartcar_id UNIQUE (smartcar_vehicle_id)
);

-- Table to cache Smartcar vehicle information
CREATE TABLE smartcar_vehicle_cache (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    smartcar_vehicle_id VARCHAR(100) NOT NULL,
    user_id UUID NOT NULL,
    vehicle_data JSONB NOT NULL, -- Full vehicle data from Smartcar
    last_updated TIMESTAMP DEFAULT NOW(),
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_smartcar_vehicle_cache_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_smartcar_vehicle_cache_smartcar_id UNIQUE (smartcar_vehicle_id)
);

-- Indexes for better performance
CREATE INDEX idx_user_smartcar_tokens_user_id ON user_smartcar_tokens(user_id);
CREATE INDEX idx_vehicle_smartcar_mapping_vehicle_id ON vehicle_smartcar_mapping(vehicle_id);
CREATE INDEX idx_vehicle_smartcar_mapping_smartcar_id ON vehicle_smartcar_mapping(smartcar_vehicle_id);
CREATE INDEX idx_vehicle_smartcar_mapping_user_id ON vehicle_smartcar_mapping(user_id);
CREATE INDEX idx_smartcar_vehicle_cache_smartcar_id ON smartcar_vehicle_cache(smartcar_vehicle_id);
CREATE INDEX idx_smartcar_vehicle_cache_user_id ON smartcar_vehicle_cache(user_id);

-- Add comments for documentation
COMMENT ON TABLE user_smartcar_tokens IS 'Stores OAuth access tokens for Smartcar API integration';
COMMENT ON TABLE vehicle_smartcar_mapping IS 'Maps internal vehicles to Smartcar vehicle IDs';
COMMENT ON TABLE smartcar_vehicle_cache IS 'Caches vehicle information from Smartcar API';
