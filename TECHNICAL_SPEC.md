# TECHNICAL_SPEC.md for RideSync

## Feature Requirements Mapping
| Feature ID | Description |
|------------|-------------|
| FR-001 | User must be able to register an account with email, unique username and password and create a "Ride Group". |
| FR-002 | Group Admin(user who created the group) can invite other users to their group via unique usernames. |
| FR-003 | create a API endpoint for GPS devices to stream location data. |
| FR-004 | User can register there own GPS device IDs and associate them with vehicles and vehicles are associated with particular user. |
| FR-005 | Backend ingests real-time location data streams into Confluent topic. |
| FR-006 | Backend consumes location data from Kafka, validates it, and prepares it for frontend. |
| FR-007 | Main dashboard features an integrated Google Map. |
| FR-008 | Dashboard establishes a WebSocket connection during an active "Ride Session". |
| FR-009 | Map updates markers for real-time location of members in the active session. |
| FR-010 | Group Admin can start and end a "Ride Session". |
| FR-011 | System analyzes real-time data for anomalous behavior using some llm with integrates with spring ai. |
| FR-012 | Detects anomalies such as stationary members or significant direction deviations. |
| FR-013 | Generates alerts for detected anomalies and sends them to the Group Admin. |
| FR-014 | Alert messages specify member's name and nature of the anomaly. |

## API Endpoints
### User Management
- **POST /api/user/register**
  - Request Body: `{ "username": "string","name": "string", "password": "string", "email": "string","vehicleName":"","vehicleRegNo":"string" }`
  - Response: `{ "userId": "string", "message": "User registered successfully." }`

### Group Management
- **POST /api/groups**
  - Request Body: `{ "groupName": "string", "adminId": "string" }`
  - Response: `{ "groupId": "string", "message": "Group created successfully." }`

- **POST /api/groups/addUser**
  - Request Body: `{ "groupId": "string", "username": "string[]" }`
  - Response: `{ "groupId": "string", "message": "user added successfully." }`

### GPS Device Management
- **POST /api/devices/register**
  - Request Body: `{ "deviceId": "string", "vehicleId": "string" }`
  - Response: `{ "message": "Device registered successfully." }`

### Location Data Ingestion
- **POST /api/location**
  - Request Body: `{ "deviceId": "string", "latitude": "float", "longitude": "float", "timestamp": "string" }`
  - Response: `{ "message": "Location data ingested successfully." }`

### WebSocket Connection
- **WebSocket /api/ride-session**
  - Description: Establishes a connection for real-time updates during an active ride session.

## Database Schema Design
### Users Table
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| user_id     | UUID      | Unique identifier for the user. |
| username     | VARCHAR   | User's username. |
| password     | VARCHAR   | User's hashed password. |
| email        | VARCHAR   | User's email address. |
| GroupId      | VARCHAR   | User's Group Id. |
| VehicleId    | VARCHAR   | Vehcile attached with user. |

### Vehicle Table
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| vehicle_id    | UUID      | Unique identifier for the vehicle. |
| vehicle_name   | VARCHAR   | Name of the vehicle. |
| user_id     | UUID      | User ID fk of user of the vehicle associated with |
| reg_no   | TimeStamp | Registration No|

### Groups Table
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| group_id    | UUID      | Unique identifier for the group. |
| group_name   | VARCHAR   | Name of the ride group. |
| admin_id     | UUID      | User ID of the Group Admin. fk of user table |
| rideStart   | TimeStamp | time when trip starts|
| rideEnd   | TimeStamp | time when trip ends|

### Devices Table
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| device_id   | VARCHAR   | Unique identifier for the GPS device. |
| vehicle_id  | UUID      | Associated vehicle ID fk of vehicle. |

### Location Data Table
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| id          | SERIAL    | Unique identifier for the location record. |
| device_id   | VARCHAR   | Associated GPS device ID fk of device. |
| latitude    | FLOAT     | Latitude of the location. |
| longitude   | FLOAT     | Longitude of the location. |
| timestamp   | TIMESTAMP | Time of the location data. |

## Validation & Error Handling Rules
- Validate user input for registration and group creation at controller level.
- Send response in specific format in whole application
- Always send http status code for success and failure response
- Handle errors for API requests with appropriate HTTP status codes (e.g., 400 for bad requests, 404 for not found).


## Authentication / Authorization Flows
- Use JWT for user authentication.
- Secure API endpoints to ensure only authorized users can access group and device management features.

## Performance & Scalability Notes
- Ensure the system can handle thousands of concurrent GPS devices.
- Optimize WebSocket connections for low-latency updates.

## Accessibility Compliance
- Ensure the frontend adheres to WCAG 2.1 AA standards for accessibility.

## Conclusion
The TECHNICAL_SPEC.md outlines the detailed requirements, API contracts, database schema, and compliance measures necessary for the successful implementation of the RideSync platform.