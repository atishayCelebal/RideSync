-- Users
CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

-- Groups
CREATE TABLE IF NOT EXISTS groups (
    group_id UUID PRIMARY KEY,
    group_name VARCHAR(150) NOT NULL UNIQUE,
    admin_user_id UUID NOT NULL REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

-- Vehicles (1:1 with users)
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id UUID PRIMARY KEY,
    vehicle_name VARCHAR(150) NOT NULL,
    reg_no VARCHAR(50) NOT NULL UNIQUE,
    owner_user_id UUID NOT NULL UNIQUE REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

-- Devices (0..1 per vehicle)
CREATE TABLE IF NOT EXISTS devices (
    device_id VARCHAR(100) PRIMARY KEY,
    description VARCHAR(255),
    status VARCHAR(30) NOT NULL,
    api_key_hash VARCHAR(255),
    vehicle_id UUID NOT NULL UNIQUE REFERENCES vehicles(vehicle_id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

-- Group Memberships
CREATE TABLE IF NOT EXISTS group_memberships (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(group_id),
    user_id UUID NOT NULL REFERENCES users(user_id),
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_group_user_unique UNIQUE (group_id, user_id)
);

-- Ride Sessions
CREATE TABLE IF NOT EXISTS ride_sessions (
    session_id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(group_id),
    ride_start TIMESTAMPTZ,
    ride_end TIMESTAMPTZ,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

-- Location Data (high-volume)
CREATE TABLE IF NOT EXISTS location_data (
    id BIGSERIAL PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL REFERENCES devices(device_id),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_location_device_time ON location_data(device_id, timestamp);

-- Anomaly Alerts
CREATE TABLE IF NOT EXISTS anomaly_alerts (
    alert_id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES ride_sessions(session_id),
    device_id VARCHAR(100) NOT NULL REFERENCES devices(device_id),
    user_id UUID REFERENCES users(user_id),
    alert_type VARCHAR(50) NOT NULL,
    message VARCHAR(500) NOT NULL,
    detected_at TIMESTAMPTZ NOT NULL,
    resolved_at TIMESTAMPTZ,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

-- Invitations (optional)
CREATE TABLE IF NOT EXISTS invitations (
    invitation_id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(group_id),
    invited_username VARCHAR(100) NOT NULL,
    token VARCHAR(200) NOT NULL,
    status VARCHAR(30) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);


