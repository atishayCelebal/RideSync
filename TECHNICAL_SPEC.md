# TECHNICAL_SPEC.md - RideSync

## Feature Mapping

### User & Group Management
- **Feature**: User registration and group creation
  - **API Endpoint**: 
    - **POST /api/users/register**
      - **Request**: { "username": "string", "password": "string", "email": "string" }
      - **Response**: { "userId": "string", "message": "User registered successfully." }

### Device Management
- **Feature**: Register GPS device
  - **API Endpoint**: 
    - **POST /api/devices/register**
      - **Request**: { "deviceId": "string", "userId": "string" }
      - **Response**: { "message": "Device registered successfully." }

### Real-time Data Ingestion
- **Feature**: Ingest location data from GPS devices
  - **API Endpoint**: 
    - **POST /api/devices/data**
      - **Request**: { "deviceId": "string", "latitude": "float", "longitude": "float", "timestamp": "string" }
      - **Response**: { "message": "Location data received." }

### Real-Time Map Dashboard
- **Feature**: Display real-time locations
  - **API Endpoint**: 
    - **GET /api/sessions/{sessionId}/locations**
      - **Request**: { "sessionId": "string" }
      - **Response**: [ { "userId": "string", "latitude": "float", "longitude": "float" }, ... ]

### Anomaly Detection
- **Feature**: Alert Group Admin on anomalies
  - **Detection Logic**: Identify if a group member is stationary for over 3 minutes or deviates significantly from the path.
  - **Alert Message**: "Alert: [Rider Name] has been stationary for 5 minutes."

## Database Schema Design

### User Table
| Column     | Type    | Description                 |
|------------|---------|-----------------------------|
| userId     | UUID    | Primary Key                 |
| username   | String  | Unique username             |
| email      | String  | User email                  |
| password   | String  | Hashed password             |

### Device Table
| Column     | Type    | Description                 |
|------------|---------|-----------------------------|
| deviceId   | UUID    | Primary Key                 |
| userId     | UUID    | Foreign Key to User         |

### Session Table
| Column     | Type    | Description                 |
|------------|---------|-----------------------------|
| sessionId  | UUID    | Primary Key                 |
| groupId    | UUID    | Foreign Key to Group        |
| startTime  | Timestamp | Session start time       |
| endTime    | Timestamp | Session end time         |

## Validation & Error Handling
- Ensure all inputs are validated against expected data types.
- Return standardized error responses for failed requests.

## Authentication / Authorization Flows
- Implement JWT for user sessions. Secure API endpoints with token-based authentication.
- Only group members can access location data during an active ride session.

## Performance & Scalability Notes
- The system must support concurrent ingestion of location data from thousands of devices with low latency.
- Use caching mechanisms where applicable to improve performance.

## Accessibility Compliance
- Ensure compliance with WCAG 2.1 AA standards to provide a user-friendly experience for all users.
