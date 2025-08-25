# RideSync Architecture Diagram

```mermaid
graph TD;
    A[User Interface (React)] -->|WebSocket| B[Backend (Java, Spring Boot)];
    B -->|API Calls| C[Database (PostgreSQL)];
    B -->|Ingests Data| D[Confluent Kafka];
    D -->|Processes Data| B;
    B -->|Sends Updates| A;