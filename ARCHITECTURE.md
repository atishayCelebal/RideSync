# ARCHITECTURE.md - RideSync

## High-Level Architecture

```mermaid
graph LR
    subgraph Frontend
        A[React App] -->|WebSocket| B[Spring Boot Backend]
        B -->|REST API| C[Confluent Streaming]
    end

    subgraph Backend
        B[Spring Boot Backend] --> D[Database (PostgreSQL)]
        E[AI Logic] --> |Real-Time Data| B
    end
```

## Component Breakdown

### Frontend
- **React**: User interface built with React to manage interactions, display maps, and real-time location updates.

### Backend
- **Spring Boot**: Implements RESTful APIs for user and group management, data ingestion, and serves real-time updates via WebSockets.
- **Confluent**: Handles real-time data ingestion and processing of location data from GPS devices.
- **PostgreSQL**: Database to manage user accounts, groups, and device information.

## Service Boundaries & Responsibilities
- Frontend interfaces with the backend through REST API (for initial data requests) and WebSockets (for real-time updates).
- Backend processes incoming GPS data and manages alerts related to user behavior during ride sessions.
- Confluent handles simultaneous input streams from multiple devices ensuring real-time performance.

## Security Enforcement
- User location data is secured and only accessible to members of the specific group during an active Ride Session.
- API endpoints are protected through authentication mechanisms (e.g., API keys, JWT).

## Summary
This architecture aims to provide a reliable, scalable, and secure method for managing group rides and tracking vehicles in real time while ensuring user privacy and data security.
