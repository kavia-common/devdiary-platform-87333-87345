# devdiary-platform-87333-87345

Backend (Spring Boot) quick start:

- Requirements: JDK 17+, Gradle wrapper.
- Configure environment variables (see devdiary_backend/ENVIRONMENT.example). In CI this is managed automatically.
- For local dev without Postgres, the app will run using in-memory H2 emulating PostgreSQL.

Run:
- cd devdiary_backend
- ./gradlew bootRun

Docs:
- Swagger UI: /swagger-ui.html
- OpenAPI: /api-docs

Auth:
- Register: POST /api/auth/register { email, displayName, password }
- Login: POST /api/auth/login { email, password } -> returns JWT
- Use "Authorization: Bearer <token>" for subsequent endpoints.

Domains:
- Users: GET /api/users/me
- Log Entries: POST /api/log-entries, GET /api/log-entries?page=0&size=20
- Summaries: POST /api/summaries { date: YYYY-MM-DD }
- Integrations: GET /api/integrations, POST /api/integrations/connect
- Activity: GET /api/activity?page=0&size=20
- Analytics: GET /api/analytics/daily?start=YYYY-MM-DD&end=YYYY-MM-DD

Database:
- To connect to devdiary_database (PostgreSQL), set POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD, DB_DRIVER=org.postgresql.Driver
- The app uses Hibernate ddl-auto=update and default schema 'public'. Set search_path at DB level if needed.