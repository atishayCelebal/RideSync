# ER Diagram for RideSync
```mermaid
erDiagram
    USER {
        UUID id PK
        String name
        String email
        String password
    }

    GROUP {
        UUID id PK
        String groupName
    }

    DEVICE {
        UUID id PK
        String deviceId
        UUID groupId FK
    }

    MEMBER {
        UUID id PK
        UUID userId FK
        UUID groupId FK
    }

    USER ||--o{ MEMBER : "can have"
    GROUP ||--o{ MEMBER : "has"
    GROUP ||--o{ DEVICE : "can have"
    USER ||--o{ GROUP : "can create"