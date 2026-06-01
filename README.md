Made with Copilot to test AI capabilities.

Prompt:

### Create a fullstack task management application.
#### Backend:
- Use Spring Boot
- Implement REST API with endpoints:
GET /api/tasks
GET /api/tasks/{id}
POST /api/tasks
PUT /api/tasks/{id}
DELETE /api/tasks/{id} 
- Controllers should only handle HTTP concerns
- Business logic belongs in services. Keep services stateless.
- Use proper HTTP status codes.
- Use @RestController.
- Use request validation annotations.
- Return appropriate HTTP status codes
- Include unit tests. Use:
JUnit 5
Mockito
Spring Boot Test

### Frontend:
- Use Angular (latest version, standalone components)
- Create a TaskListComponent with:
Task list display
Create task form (Reactive Forms)
Toggle task completion
Delete task
- Use HttpClient to connect to backend
- Handle loading and error states
- Use proper typing with interfaces
- Include unit tests for components and services

### API Integration Standards
- Frontend communicates with backend via REST APIs.
- Use consistent response structures.
- Backend errors should include:
message
status
timestamp
validation details when applicable
- Ensure frontend and backend contracts match
