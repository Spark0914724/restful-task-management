# Task Manager API

A RESTful task management service built with Kotlin and Spring Boot. Uses Spring WebFlux for reactive HTTP handling, JdbcClient with native SQL for data access, and Flyway for database migrations.

## Tech Stack

- Kotlin 1.9 / JDK 21
- Spring Boot 3.5
- Spring WebFlux (Netty)
- Spring JDBC (JdbcClient)
- Project Reactor (Mono)
- H2 in-memory database
- Flyway

## Project Structure

```
src/main/kotlin/com/taskmanager/task_manager/
├── controller/        # REST endpoints
├── service/           # Business logic, Reactor wrappers
├── repository/        # JdbcClient + native SQL
├── model/             # Task entity, TaskStatus enum
├── dto/               # Request/response DTOs, mapper
└── exception/         # TaskNotFoundException, GlobalExceptionHandler
```

## Prerequisites

- JDK 21

## Running the Application

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`.

## Running Tests

```bash
./gradlew test
```

Covers service layer (7 tests) and controller layer (8 tests).

## API Reference

### Create a task
```
POST /api/tasks
```
```json
{
  "title": "Prepare report",
  "description": "Monthly financial report"
}
```
Response: `201 Created`
```json
{
  "id": 1,
  "title": "Prepare report",
  "description": "Monthly financial report",
  "status": "NEW",
  "createdAt": "2026-03-26T12:00:00",
  "updatedAt": "2026-03-26T12:00:00"
}
```

---

### Get all tasks
```
GET /api/tasks?page=0&size=10&status=NEW
```
- `page` and `size` are required
- `status` is optional — one of `NEW`, `IN_PROGRESS`, `DONE`, `CANCELLED`
- Results are sorted by `createdAt` descending

Response: `200 OK`
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

### Get task by ID
```
GET /api/tasks/{id}
```
Response: `200 OK` or `404 Not Found`

---

### Update task status
```
PATCH /api/tasks/{id}/status
```
```json
{
  "status": "DONE"
}
```
Response: `200 OK` with updated task

---

### Delete a task
```
DELETE /api/tasks/{id}
```
Response: `204 No Content`

## Validation

- `title` is required, must be between 3 and 100 characters
- Invalid requests return `400 Bad Request` with field-level error messages

## Notes

- Database is in-memory (H2), data resets on restart
- H2 console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskdb`)
