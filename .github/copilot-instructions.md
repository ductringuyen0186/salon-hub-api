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

#### 6. **Test Data Management**
- Use test fixtures for reusable test data
- Create builders for complex test objects
- Use meaningful test data that reflects real scenarios

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
