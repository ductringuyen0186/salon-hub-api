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
