# Copilot Instructions for SalonHub API

## Project Structure

Follow this exact structure for all new code:

```
src/main/java/com/salonhub/api/
├── Application.java
├── [domain]/
│   ├── controller/
│   ├── service/
│   ├── model/
│   ├── repository/
│   ├── dto/
│   └── mapper/
```

Example based on appointment directory structure:
```
src/main/java/com/salonhub/api/appointment/
├── controller/
├── dto/
├── mapper/
├── model/
├── repository/
└── service/
```

## Testing Requirements

**ALL TESTS MUST PASS** before any code changes are committed.

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run integration tests (uses Docker/Testcontainers)
./gradlew integrationTest

# Run all tests
./gradlew check
```

**Note for Windows PowerShell:** Use semicolon (`;`) instead of `&&` for command chaining:
```powershell
cd project-directory; .\gradlew.bat check
```

### Test Structure

- **Unit tests**: `src/test/java/com/salonhub/api/`
- **Integration tests**: `src/integration/java/com/salonhub/api/`
- **Test fixtures**: `src/testFixtures/java/com/salonhub/api/`

Integration tests use Testcontainers with MySQL for database testing.

## Database Changes

**Always use Flyway migrations for database changes:**

1. Create new migration file: `src/main/resources/db/migration/V[N]__description.sql`
2. Follow naming convention: `V7__add_new_table.sql`
3. Never modify existing migration files
4. Test migrations with both unit and integration tests

## Development Workflow

1. **Create a new branch** for each feature:
   ```bash
   git checkout -b feature/short-name
   # Example: git checkout -b feature/checkin-service
   ```
2. Write failing tests first
3. Implement feature
4. Ensure all tests pass: `./gradlew check`
5. Run application: `./gradlew bootRun`
6. Test with Docker: `docker-compose up --build`
7. **Never commit directly to main branch**
