#!/bin/bash

# User Registration
curl -X POST http://localhost:8080/api/users/register \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "password": "password123",
  "groupName": "My Ride Group"
}'

# Device Registration
curl -X POST http://localhost:8080/api/devices/register \
-H "Content-Type: application/json" \
-d '{
  "deviceId": "GPS12345"
}'

# Ingest Location Data
curl -X POST http://localhost:8080/api/devices/data \
-H "Content-Type: application/json" \
-d '{
  "deviceId": "GPS12345",
  "latitude": 20.5937,
  "longitude": 78.9629,
  "timestamp": "2025-08-25T12:00:00Z"
}'

# Start Ride Session
curl -X POST http://localhost:8080/api/sessions/start \
-H "Content-Type: application/json" \
-d '{
  "groupId": "group-id"
}'

# End Ride Session
curl -X POST http://localhost:8080/api/sessions/end \
-H "Content-Type: application/json" \
-d '{
  "groupId": "group-id"
}'