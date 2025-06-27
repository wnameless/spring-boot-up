# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **spring-boot-up**, a Spring Boot utility library providing common enterprise application features. It's designed to be included as a dependency in other Spring Boot projects rather than run as a standalone application.

- **Group ID:** `com.github.wnameless.spring`
- **Artifact ID:** `spring-boot-up`
- **Version:** 0.14.14-SNAPSHOT
- **Java:** 21
- **Spring Boot:** 3.5.3
- **Build Tool:** Maven
- **License:** Apache 2.0

## Common Commands

### Build and Package
```bash
mvn clean compile
mvn clean package
mvn clean install
```

### Testing
```bash
mvn test
mvn test -Dtest=ClassName
```

### Code Quality
```bash
mvn jacoco:report  # Generate code coverage report
mvn javadoc:javadoc  # Generate JavaDoc
```

### Deployment (for maintainers)
```bash
mvn clean deploy  # Deploy to Sonatype OSSRH
```

## Architecture Overview

The library is organized into functional modules under `com.github.wnameless.spring.boot.up`:

- **actioncode** - Action code management with validation and expiration
- **async** - Asynchronous task execution utilities  
- **attachment** - File attachment handling with snapshot capabilities
- **autocreation** - Automated entity creation services
- **fsm** - Finite State Machine implementation for workflow management
- **jsf** - JSON Schema Form utilities with React integration
- **logviewer** - Built-in log viewing functionality
- **membership** - User and organizational membership management
- **messageboard** - Notice and messaging system
- **notification** - Configurable notification strategies
- **permission** - Role-based access control with abilities framework
- **selectionflow** - Multi-step selection processes
- **tagging** - Label and tag management system
- **thymeleaf** - Thymeleaf template extensions
- **validation** - Custom validation utilities
- **web** - RESTful controllers with CRUD operations

## Key Technologies

- **Backend:** Spring Boot, Spring Data MongoDB, Spring Validation
- **Frontend:** Thymeleaf templates, HTMX 2.0.3, React JSON Schema Forms
- **Styling:** Bootstrap/Bootswatch themes
- **Build:** Maven with standard Spring Boot parent
- **Documentation:** JavaDoc generated via Maven plugin

## Development Notes

- This is a library project - no main application class or standalone execution
- Uses MongoDB as the primary data store for most modules
- Extensive use of Lombok for reducing boilerplate code
- Templates are organized by module in `src/main/resources/templates/`
- Static resources include React components built with Webpack
- Limited test coverage - opportunity for improvement
- Frontend build tools located in `src/main/resources/static/react-form/bs-dev/`

## Package Structure

All classes follow the pattern: `com.github.wnameless.spring.boot.up.[module].*`

Each module typically contains:
- Entity/Model classes
- Repository interfaces (Spring Data)
- Service classes
- Controller classes (REST endpoints)
- Configuration classes
- Thymeleaf templates in corresponding template directories