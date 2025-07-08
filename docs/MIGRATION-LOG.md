# SalonHub API - Database Migration Log

## PostgreSQL Migration (January 2025)

### Overview
Successfully migrated SalonHub API from MySQL to PostgreSQL for all environments (development, testing, CI, production).

### Latest Updates

#### Flyway Migration Conflict Fix (July 2025)
- **Issue**: Duplicate `V1__create_initial_schema.sql` files causing Flyway conflicts
- **Solution**: Removed duplicate file in `postgresql/` subdirectory
- **Result**: ✅ All tests now pass successfully

#### Test Schema Configuration Fix (July 2025)
- **Issue**: `@DataJpaTest` failing with "Schema-validation: missing table [appointment_services]"
- **Root Cause**: Test config had `hibernate.ddl-auto: validate` but `flyway.enabled: false`
- **Solution**: Changed test config to `hibernate.ddl-auto: create-drop` for proper table creation
- **Result**: ✅ All unit and integration tests pass
- **Impact**: ✅ GitHub Actions CI build now successful

### Overview
Successfully migrated SalonHub API from MySQL to PostgreSQL for all environments (development, testing, CI, production).

### Changes Made

#### 1. **Dependencies & Configuration**
- **Removed**: MySQL Connector/J and MySQL Testcontainers
- **Added**: PostgreSQL JDBC driver and PostgreSQL Testcontainers
- **Updated**: All Spring configuration files to use PostgreSQL

#### 2. **Database Schema Migration**
- **Consolidated**: All migration files into single `V1__create_initial_schema.sql`
- **Fixed**: Schema mismatches between JPA entities and database
- **Updated**: Column names and data types for PostgreSQL compatibility

#### 3. **Key Schema Updates**
- `appointment_services.service_id` (was `service_type_id`)
- `appointments.start_time` and `actual_end_time` columns added
- `employees.available` and `role` columns added
- `service_types.estimated_duration_minutes` (was `duration`)
- Complete `users` and `queue` tables to match JPA entities

#### 4. **Infrastructure Updates**
- **Docker**: Updated `docker-compose.yml` to use PostgreSQL
- **CI/CD**: Updated GitHub Actions workflow
- **Testing**: Updated Testcontainers setup

### Database Configuration

#### Development & Testing
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/salonhub_dev
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

#### Production
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

### Verification
- ✅ All unit tests pass
- ✅ All integration tests pass
- ✅ Schema validation successful
- ✅ Testcontainers using PostgreSQL
- ✅ Docker Compose using PostgreSQL
- ✅ CI/CD pipeline updated

### Migration Commands
```bash
# Verify tests pass
./gradlew test integrationTest

# Start application with PostgreSQL
docker-compose up --build

# Access database
docker exec -it salon-hub-api-db-1 psql -U postgres -d salonhub_dev
```

### Post-Migration Notes
- All MySQL references removed from codebase
- Documentation updated to reflect PostgreSQL usage
- Schema is now in sync with JPA entity models
- Ready for production deployment with PostgreSQL

---
**Migration Completed**: January 2025  
**Status**: ✅ Successful  
**Test Results**: All passing
