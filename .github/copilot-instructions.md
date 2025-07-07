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
5. **Run application with Docker** (automatically starts containers if not running):
   ```powershell
   # Smart startup script - checks if containers are running and starts them if needed
   $containers = docker ps --filter "name=salon-hub-api" --format "table {{.Names}}"
   if (-not ($containers -match "salon-hub-api-db-1" -and $containers -match "salon-hub-api-app-1")) {
       Write-Host "Starting SalonHub containers..."
       .\gradlew.bat bootJar
       docker-compose up --build -d
       Write-Host "Waiting for application to start..."
       Start-Sleep -Seconds 20
       Write-Host "Application should be ready at http://localhost:8082"
   } else {
       Write-Host "Containers already running!"
       docker ps --filter "name=salon-hub-api"
   }
   ```
   
   **If containers are restarting or failing to start:**
   ```powershell
   # Complete database reset (fixes Flyway migration issues)
   docker-compose down -v
   docker system prune -f
   docker volume prune -f
   .\gradlew.bat bootJar
   docker-compose up --build
   ```
   
   **OR for local development without Docker:**
   ```bash
   ./gradlew bootRun
   ```
6. **Never commit directly to main branch**

## Git Commit Guidelines

**IMPORTANT**: Only commit and push changes when explicitly requested in the chat conversation.

- **Default behavior**: Make code changes without committing
- **Commit only when**: The user specifically asks to commit or push changes
- **Auto-commit exceptions**: Critical fixes that break builds or tests may be committed immediately
- **Branch management**: Always work on feature branches, never directly on main

This allows for iterative development and gives the user control over when changes are persisted.

## New Feature Testing Requirements

**EVERY NEW FEATURE MUST INCLUDE COMPREHENSIVE TESTS**

### Required Test Coverage

For each new feature, you MUST implement:

#### 1. **Unit Tests** (Required)
- **Controller Tests**: Test all endpoints, validation, error handling
- **Service Tests**: Test business logic, edge cases, exceptions
- **Repository Tests**: Test data access if custom queries exist
- **DTO/Mapper Tests**: Test data transformation logic

#### 2. **Integration Tests** (Required)
- **End-to-End API Tests**: Test complete request/response flow with real database
- **Database Integration**: Uses `@ServerSetupExtension` with seeded test data
- **Security Integration**: Test authentication/authorization if applicable
- **Ordered Test Execution**: Use `@TestMethodOrder` for CRUD operations (Create → Read → Update → Delete)

#### 3. **Test Structure Example**
```
src/test/java/com/salonhub/api/[feature]/
├── controller/
│   └── [Feature]ControllerTest.java
├── service/
│   └── [Feature]ServiceTest.java
└── repository/
    └── [Feature]RepositoryTest.java

src/integration/java/com/salonhub/api/[feature]/
└── [Feature]IntegrationTest.java
```

#### 4. **Test Configuration Requirements**
- Use `@WebMvcTest` for controller tests
- Import `TestSecurityConfig` for security-related tests
- Use `@DataJpaTest` for repository tests
- Use `@ServerSetupExtension` for integration tests (provides database setup)
- Use `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` for ordered integration tests
- Use database defaults from `CustomerDatabaseDefault` and `EmployeeDatabaseDefault`

#### 5. **Test Coverage Standards**
- **Minimum 80% code coverage** for new features
- **100% coverage** for critical business logic
- Test all error scenarios and edge cases
- Test validation rules and constraints

#### 6. **Test Fixtures and Database Defaults**
- **Always use test fixtures** for reusable test data and tools
- **Create database defaults** for new tables or updated columns
- **Follow the pattern** of existing defaults like `EmployeeDatabaseDefault`
- **Update associated files** when database schema changes

**Test Fixture Structure:**
```
src/testFixtures/java/com/salonhub/api/
├── [domain]/
│   ├── [Domain]DatabaseDefault.java
│   ├── [Domain]TestDataBuilder.java
│   └── [Domain]TestFixtures.java
└── ServerSetupExtension.java
```

**Database Default Pattern (following EmployeeDatabaseDefault):**
```java
public class FeatureDatabaseDefault {
    
    // Entity IDs (use consistent numbering)
    public static final Long FEATURE_ID_1 = 1L;
    public static final Long FEATURE_ID_2 = 2L;
    
    // Entity names/references
    public static final String FEATURE_NAME_1 = "Feature One";
    public static final String FEATURE_NAME_2 = "Feature Two";
    
    // SQL insert statements for test data
    public static final String INSERT_FEATURE_1 = 
        "INSERT INTO features (id, name, description, created_at) VALUES " +
        "(1, 'Feature One', 'Test feature description', NOW())";
        
    public static final String INSERT_FEATURE_2 = 
        "INSERT INTO features (id, name, description, created_at) VALUES " +
        "(2, 'Feature Two', 'Another test feature', NOW())";
    
    // Array of all inserts for batch operations
    public static final String[] ALL_INSERTS = {
        INSERT_FEATURE_1,
        INSERT_FEATURE_2
    };
}
```

**When to Update Test Fixtures:**
- **New table created**: Create new `[Domain]DatabaseDefault.java`
- **Column added/updated**: Update existing database defaults
- **Foreign key relationships**: Update related database defaults
- **Migration changes**: Ensure test data matches schema

**Test Data Builder Pattern:**
```java
public class FeatureTestDataBuilder {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    
    public static FeatureTestDataBuilder aFeature() {
        return new FeatureTestDataBuilder()
            .withId(1L)
            .withName("Test Feature")
            .withDescription("Test Description")
            .withCreatedAt(LocalDateTime.now());
    }
    
    public FeatureTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public FeatureTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public Feature build() {
        Feature feature = new Feature();
        feature.setId(id);
        feature.setName(name);
        feature.setDescription(description);
        feature.setCreatedAt(createdAt);
        return feature;
    }
    
    public FeatureRequestDTO buildRequestDTO() {
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }
}
```

#### 7. **Test Data Management**
- Use test fixtures for reusable test data
- Create builders for complex test objects
- Use meaningful test data that reflects real scenarios
- Always reference existing database defaults when possible

### Example Test Implementation

```java
// Controller Test Example
@WebMvcTest(controllers = FeatureController.class)
@Import(TestSecurityConfig.class)
class FeatureControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private FeatureService service;
    
    @Test
    void whenValidRequest_thenReturns200() throws Exception {
        // Test implementation
    }
    
    @Test
    void whenInvalidRequest_thenReturns400() throws Exception {
        // Test validation
    }
}

// Integration Test Example
@ServerSetupExtension
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FeatureIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Use constants from database defaults
    private static final Long EXISTING_CUSTOMER_ID = CustomerDatabaseDefault.JANE_ID;
    private static final Long EXISTING_EMPLOYEE_ID = EmployeeDatabaseDefault.ALICE_ID;
    
    private static Long createdEntityId;
    
    @Test
    @Order(1)
    void createEntity_shouldReturnOkAndId() throws Exception {
        FeatureRequestDTO req = new FeatureRequestDTO();
        req.setCustomerId(EXISTING_CUSTOMER_ID);
        req.setEmployeeId(EXISTING_EMPLOYEE_ID);
        
        var result = mockMvc.perform(post("/api/features")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andReturn();
            
        String responseJson = result.getResponse().getContentAsString();
        FeatureResponseDTO resp = objectMapper.readValue(responseJson, FeatureResponseDTO.class);
        createdEntityId = resp.getId();
    }
    
    @Test
    @Order(2)
    void getEntity_shouldReturnEntity() throws Exception {
        mockMvc.perform(get("/api/features/{id}", createdEntityId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdEntityId));
    }
    
    @Test
    @Order(3)
    void updateEntity_shouldReturnUpdated() throws Exception {
        FeatureRequestDTO updateReq = new FeatureRequestDTO();
        updateReq.setCustomerId(EXISTING_CUSTOMER_ID);
        updateReq.setEmployeeId(EXISTING_EMPLOYEE_ID);
        
        mockMvc.perform(put("/api/features/{id}", createdEntityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdEntityId));
    }
    
    @Test
    @Order(4)
    void deleteEntity_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/features/{id}", createdEntityId))
            .andExpect(status().isNoContent());
    }
}
```

### Test Execution Before Commit

Before committing any new feature:

```bash
# Run all tests
./gradlew check

# Run with coverage report
./gradlew test jacocoTestReport

# Run integration tests
./gradlew integrationTest
```

**Remember**: Features without proper tests will be rejected in code review!

## Flyway Migration Troubleshooting

**CRITICAL**: When encountering Flyway migration checksum conflicts, follow this exact procedure:

### Common Flyway Errors

#### 1. Migration Checksum Mismatch

**Error Pattern:**
```
FlywayValidateException: Validate failed: Migrations have failed validation
Migration checksum mismatch for migration version X
-> Applied to database : [checksum1]
-> Resolved locally    : [checksum2]
Either revert the changes to the migration, or run repair to update the schema history.
```

**Root Cause:** Migration file was modified after being applied to database.

#### 2. Failed Migration Detection

**Error Pattern:**
```
FlywayValidateException: Validate failed: Migrations have failed validation
Detected failed migration to version X (description).
Please remove any half-completed changes then run repair to fix the schema history.
```

**Root Cause:** Previous migration attempt failed, leaving database in inconsistent state.

### **SOLUTION: Complete Database Reset (Development Only)**

**⚠️ WARNING: This destroys all database data. Only use in development environments.**

#### Step 1: Stop and Clean Docker Containers

```powershell
# Stop all containers and remove volumes
docker-compose down -v

# Clean Docker system (removes unused containers, networks, images)
docker system prune -f
docker volume prune -f
```

#### Step 2: Verify Migration Files

Check migration files for syntax errors:

```powershell
# Navigate to migration directory
cd src\main\resources\db\migration

# List all migration files
dir V*.sql
```

**Common MySQL Migration Syntax Issues:**
- Use `MODIFY COLUMN` instead of `ALTER COLUMN` for MySQL
- Ensure proper semicolon termination
- Use correct MySQL data types

**Example of Correct V6 Migration:**
```sql
-- Add guest column to customers table
ALTER TABLE customers 
ADD COLUMN guest BOOLEAN DEFAULT FALSE NOT NULL;

-- Make email column nullable for guest users
ALTER TABLE customers 
MODIFY COLUMN email VARCHAR(255) NULL;
```

#### Step 3: Rebuild and Restart

```powershell
# Rebuild the application JAR
.\gradlew.bat bootJar

# Start fresh containers with clean database
docker-compose up --build
```

#### Step 4: Verify Success

```powershell
# Check container status
docker ps

# Check application logs
docker logs salon-hub-api-app-1 --tail 20

# Test API endpoint
Invoke-WebRequest -Uri "http://localhost:8082/v3/api-docs" -UseBasicParsing
```

**Success Indicators:**
- Application logs show: `Started Application in X.X seconds`
- API returns HTTP 200 status
- No Flyway validation errors in logs

### **Alternative: Flyway Configuration Fix (Advanced)**

If you need to preserve data, add to `application.yml`:

```yaml
spring:
  flyway:
    clean-disabled: false
    baseline-on-migrate: true
    # For development only - allows schema recreation
```

### **Prevention Best Practices**

1. **Never modify applied migration files** - Always create new migrations
2. **Test migrations locally** before committing
3. **Use consistent SQL syntax** for target database (MySQL 8.4)
4. **Run integration tests** that verify migration success
5. **Clean Docker volumes** between major database changes

### **Emergency Commands**

```powershell
# Force clean everything Docker-related
docker-compose down -v; docker system prune -af; docker volume prune -f

# Rebuild from scratch
.\gradlew.bat clean bootJar; docker-compose up --build

# Check application health
Start-Sleep -Seconds 15; docker logs salon-hub-api-app-1 --tail 10
```

### **Docker Management from VS Code**

**You can manage the entire stack from VS Code without opening Docker Desktop:**

```powershell
# Start application stack
docker-compose up --build

# Stop application stack  
docker-compose down

# View logs
docker logs salon-hub-api-app-1
docker logs salon-hub-api-db-1

# Restart just the app (after code changes)
docker-compose restart app
```

**VS Code Tasks Available:**
- `Docker: Start Application` - Starts both containers in background
- `Docker: Stop Application` - Stops all containers

**Access Points:**
- API Base URL: `http://localhost:8082`
- Swagger UI: `http://localhost:8082/swagger-ui/index.html`
- OpenAPI Docs: `http://localhost:8082/v3/api-docs`
- Database: `localhost:3306` (root/root)

## Running the Application

### **Recommended: Full Stack with Docker (Automatic)**

**Single command to start everything (build + containers):**
```powershell
.\gradlew.bat bootJar; docker-compose up --build
```

**Smart startup (only starts containers if needed):**
```powershell
# PowerShell script to check and start containers automatically
$containers = docker ps --filter "name=salon-hub-api" --format "table {{.Names}}"
if (-not ($containers -match "salon-hub-api-db-1" -and $containers -match "salon-hub-api-app-1")) {
    Write-Host "Starting SalonHub containers..."
    .\gradlew.bat bootJar
    docker-compose up --build -d
    Write-Host "Waiting for application to start..."
    Start-Sleep -Seconds 20
    Write-Host "Application should be ready at http://localhost:8082"
} else {
    Write-Host "Containers already running!"
    docker ps --filter "name=salon-hub-api"
}
```

**If application containers are restarting or failing:**
```powershell
# Check container status first
docker ps --filter "name=salon-hub-api"

# If containers are restarting, check logs
docker logs salon-hub-api-app-1 --tail 30

# Complete database reset (fixes Flyway migration and database connection issues)
docker-compose down -v
docker system prune -f
docker volume prune -f
.\gradlew.bat bootJar
docker-compose up --build

# Wait for startup and verify
Start-Sleep -Seconds 30
Invoke-WebRequest -Uri "http://localhost:8082/v3/api-docs" -UseBasicParsing | Select-Object StatusCode
```

### **Development Options**

#### Option 1: Full Docker Stack (Recommended for testing)
```powershell
# Complete rebuild and restart
.\gradlew.bat bootJar
docker-compose up --build

# Background mode (non-blocking)
.\gradlew.bat bootJar
docker-compose up --build -d
```

#### Option 2: Local Spring Boot with Docker Database
```powershell
# Start only database container
docker-compose up db -d

# Run Spring Boot locally (connects to Docker database)
.\gradlew.bat bootRun
```

#### Option 3: Quick Container Restart (after code changes)
```powershell
# Rebuild JAR and restart app container only
.\gradlew.bat bootJar
docker-compose restart app
```

### **Troubleshooting Container Issues**

**Common Issues and Solutions:**

#### 1. **Application Container Restarting**
**Symptoms:** Container shows `Restarting (1) Less than a second ago`
**Cause:** Usually database connection issues or Flyway migration problems

**Solution:**
```powershell
# Check application logs for specific error
docker logs salon-hub-api-app-1 --tail 50

# If you see "Communications link failure" or Flyway errors:
docker-compose down -v
docker system prune -f
docker volume prune -f
.\gradlew.bat bootJar
docker-compose up --build
```

#### 2. **Database Connection Timeout**
**Symptoms:** `Unable to obtain connection from database: Communications link failure`
**Cause:** Application starts before database is fully initialized

**Solution:**
```powershell
# Stop and restart with proper timing
docker-compose down
docker-compose up db -d
Start-Sleep -Seconds 15  # Wait for database
docker-compose up app
```

#### 3. **Flyway Migration Issues**
**Symptoms:** `Migration checksum mismatch` or `Detected failed migration`
**Cause:** Migration files modified or database in inconsistent state

**Solution:** Use complete database reset (see Flyway section above)

### **Health Checks and Verification**

```powershell
# Check container status
docker ps --filter "name=salon-hub-api"

# View application logs
docker logs salon-hub-api-app-1 --tail 20

# Test API health
Invoke-WebRequest -Uri "http://localhost:8082/v3/api-docs" -UseBasicParsing | Select-Object StatusCode

# Quick browser test
Start-Process "http://localhost:8082/swagger-ui/index.html"
```

**Application Ready Indicators:**
- Console shows: `Started Application in X.X seconds`
- API docs return HTTP 200: `http://localhost:8082/v3/api-docs`
- Swagger UI loads: `http://localhost:8082/swagger-ui/index.html`
