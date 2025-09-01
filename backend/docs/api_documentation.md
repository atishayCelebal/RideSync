# API Documentation for RideSync

## User Endpoints
### POST /users
- **Description**: Creates a new user
- **Request Body**: UserDTO
- **Response**: User instance

### GET /users/{id}
- **Description**: Retrieves user by ID
- **Response**: User instance or 404 Not Found

### DELETE /users/{id}
- **Description**: Deletes user by ID
- **Response**: 204 No Content

## Group Endpoints
### POST /groups
- **Description**: Creates a new group
- **Request Body**: GroupDTO
- **Response**: Group instance

### GET /groups/{id}
- **Description**: Retrieves group by ID
- **Response**: Group instance or 404 Not Found

### DELETE /groups/{id}
- **Description**: Deletes group by ID
- **Response**: 204 No Content

## Device Endpoints
### POST /devices
- **Description**: Registers a new device
- **Request Body**: DeviceDTO
- **Response**: Device instance

### GET /devices/{id}
- **Description**: Retrieves device by ID
- **Response**: Device instance or 404 Not Found

### DELETE /devices/{id}
- **Description**: Deletes device by ID
- **Response**: 204 No Content

## Member Endpoints
### POST /members
- **Description**: Adds a new member to a group
- **Request Body**: MemberDTO
- **Response**: Member instance

### GET /members/{id}
- **Description**: Retrieves member by ID
- **Response**: Member instance or 404 Not Found

### DELETE /members/{id}
- **Description**: Deletes member by ID
- **Response**: 204 No Content