# Rental Cars Spring API - Technical Documentation

## Overview
This document provides comprehensive technical information about the Rental Cars Spring API, a Java Spring Boot application designed for managing vehicle rental operations. The system implements a RESTful API with CQRS pattern, domain-driven design principles, and comprehensive test coverage.

## Architecture Overview

### Technology Stack
- **Framework**: Spring Boot 3.2.10
- **Language**: Java 17
- **Database**: H2 (in-memory) with PostgreSQL support
- **ORM**: Spring Data JPA with Hibernate
- **Database Migration**: Liquibase
- **Security**: Spring Security with OAuth2 Resource Server
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Testing**: JUnit 5, Spring Boot Test, Spring Security Test
- **Build Tool**: Maven
- **Utilities**: Lombok, Jackson

### Architecture Pattern
- **CQRS (Command Query Responsibility Segregation)**: Separate read and write models
- **Domain-Driven Design (DDD)**: Clean architecture with domain-centric design
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation

## Project Structure

```
rental-cars-spring-api/
├── src/main/java/
│   └── com/challenge/rental_cars_spring_api/
│       ├── RentalCarsSpringApiApplication.java    # Main application class
│       ├── config/                                # Configuration classes
│       │   └── SecurityConfig.java                 # Spring Security configuration
│       ├── access/                                 # REST Controllers (API Layer)
│       │   ├── CarrosRestController.java          # Car management endpoints
│       │   └── AluguelRestController.java         # Rental management endpoints
│       ├── core/                                   # Domain layer
│       │   ├── domain/                            # Domain entities
│       │   │   ├── Carro.java                     # Car entity
│       │   │   ├── Cliente.java                   # Customer entity
│       │   │   └── Aluguel.java                   # Rental entity
│       │   ├── queries/                           # Query handlers (CQRS Read)
│       │   │   ├── ListarCarrosQuery.java
│       │   │   └── ListarAlugueisQuery.java
│       │   │   └── dtos/                        # Data transfer objects
│       │   └── usecases/                        # Business logic (CQRS Write)
│       │       └── ProcessarAluguelRtnService.java
│       └── infrastructure/                      # Infrastructure layer
│           └── repositories/                    # JPA repositories
├── src/main/resources/
│   ├── application.properties                  # Application configuration
│   ├── liquibase/                             # Database migration scripts
│   └── RentReport.rtn                         # Sample RTN file
└── src/test/                                  # Test classes
```

## Database Schema

### Entities and Relationships

#### Carro (Car)
- **Table**: `carro`
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Columns**:
  - `modelo` (VARCHAR(50), NOT NULL) - Car model
  - `ano` (VARCHAR(4), NOT NULL) - Year of manufacture
  - `qtd_passageiros` (BIGINT, NOT NULL) - Passenger capacity
  - `km` (BIGINT, NOT NULL) - Current mileage
  - `fabricante` (VARCHAR(50), NOT NULL) - Manufacturer
  - `vlr_diaria` (DECIMAL(7,2), NOT NULL) - Daily rental rate

#### Cliente (Customer)
- **Table**: `cliente`
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Columns**:
  - `nome` (VARCHAR(100), NOT NULL) - Full name
  - `cpf` (VARCHAR(11), NOT NULL, UNIQUE) - Tax ID
  - `cnh` (VARCHAR(11), NOT NULL, UNIQUE) - Driver's license
  - `telefone` (VARCHAR(13), NOT NULL) - Phone number

#### Aluguel (Rental)
- **Table**: `aluguel`
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Foreign Keys**:
  - `carro_id` → `carro.id`
  - `cliente_id` → `cliente.id`
- **Columns**:
  - `data_aluguel` (DATE, NOT NULL) - Rental start date
  - `data_devolucao` (DATE, NOT NULL) - Return date
  - `valor` (DECIMAL(7,2), NOT NULL) - Total rental value
  - `pago` (BOOLEAN, NOT NULL) - Payment status

## API Endpoints

### Base URL
```
http://localhost:8080/api
```

### Car Management Endpoints

#### GET /api/carros
**Description**: List all cars in the fleet
**Response**: List of car details
```json
[
  {
    "id": 1,
    "modelo": "Gol",
    "ano": "2022",
    "qtdPassageiros": 5,
    "km": 15000,
    "fabricante": "Volkswagen",
    "vlrDiaria": 89.90
  }
]
```

### Rental Management Endpoints

#### POST /api/alugueis/processar
**Description**: Process RTN file and populate rental data
**Parameters**:
- `nomeArquivo` (query parameter, optional): RTN filename (default: "RentReport.rtn")
**Response**:
```json
{
  "sucesso": true,
  "registrosProcessados": 5,
  "registrosIgnorados": 0,
  "mensagem": "Processamento concluído com sucesso"
}
```

#### GET /api/alugueis/listar
**Description**: List all rentals with detailed information
**Response**:
```json
{
  "alugueis": [
    {
      "dataAluguel": "2024-01-15",
      "modeloCarro": "Gol",
      "kmCarro": 15000,
      "nomeCliente": "João Silva",
      "telefoneCliente": "+55(11)98765-4321",
      "dataDevolucao": "2024-01-20",
      "valor": 449.50,
      "pago": "SIM"
    }
  ],
  "valorTotalNaoPago": 1247.30
}
```

## RTN File Format

### Structure
- **File Type**: Fixed-width text file
- **Line Length**: 20 characters per line
- **Encoding**: ASCII/UTF-8

### Field Positions
| Position | Field | Type | Description |
|----------|-------|------|-------------|
| 1-2 | carroId | Integer | Car ID (foreign key to carro table) |
| 3-4 | clienteId | Integer | Customer ID (foreign key to cliente table) |
| 5-12 | dataAluguel | Date (YYYYMMDD) | Rental start date |
| 13-20 | dataDevolucao | Date (YYYYMMDD) | Return date |

### Example
```
01022024011520240120
```
- Car ID: 01
- Customer ID: 02
- Rental Date: 2024-01-15
- Return Date: 2024-01-20

## Configuration

### Application Properties
```properties
# Server Configuration
spring.application.name=rental-cars-spring-api
spring.mvc.servlet.path=/api

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Liquibase Configuration
spring.liquibase.change-log=classpath:liquibase/changelog.xml
```

### Maven Dependencies
Key dependencies include:
- Spring Boot Starter Web (REST API)
- Spring Boot Starter Data JPA (Database access)
- Spring Boot Starter Security (Authentication/Authorization)
- H2 Database (In-memory database)
- Liquibase (Database migrations)
- SpringDoc OpenAPI (API documentation)
- Lombok (Code generation)

## Testing

### Test Structure
- **Unit Tests**: Domain entity tests
- **Integration Tests**: API endpoint tests with H2 database
- **Test Configuration**: Separate test configuration for integration tests

### Test Classes
- `CarrosIntegrationTest`: Tests car listing functionality
- `AlugueisListarIntegrationTest`: Tests rental listing functionality
- `AlugueisProcessarIntegrationTest`: Tests RTN file processing
- `H2ConfigIntegrationTest`: Database configuration tests

### Running Tests
```bash
mvn test
```

## Development Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Build and Run
```bash
# Clone the repository
git clone [repository-url]
cd rental-cars-spring-api

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Accessing the API
- **Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **H2 Console**: http://localhost:8080/api/h2-console

## Error Handling

### Global Exception Handling
- **Validation Errors**: 400 Bad Request with detailed error messages
- **Resource Not Found**: 404 Not Found
- **Internal Server Errors**: 500 Internal Server Error with error details

### Logging
- **Framework**: SLF4J with Logback
- **Levels**: INFO, WARN, ERROR
- **Configuration**: Logback configuration in application properties

## Security

### Authentication
- **Type**: OAuth2 Resource Server
- **Token Format**: JWT (JSON Web Tokens)
- **Endpoints**: All endpoints secured by default

### Authorization
- **Role-Based Access**: Configurable via Spring Security
- **CORS Configuration**: Configured for frontend integration

## Performance Considerations

### Database Optimization
- **Indexes**: Primary keys and foreign keys automatically indexed
- **Connection Pooling**: HikariCP for connection management
- **Query Optimization**: JPA query optimization

### Caching
- **Level 1 Cache**: Hibernate first-level cache enabled
- **Query Cache**: Configurable for frequently accessed data

## Deployment

### Container Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/rental-cars-spring-api-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rental_cars
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password

# Server Configuration
SERVER_PORT=8080
```

## Monitoring and Observability

### Health Checks
- **Endpoint**: GET /api/actuator/health
- **Database Health**: Database connectivity check
- **Disk Space**: Available disk space monitoring

### Metrics
- **Framework**: Spring Boot Actuator
- **Endpoint**: GET /api/actuator/metrics
- **Custom Metrics**: Business metrics for rental operations

## Troubleshooting

### Common Issues
1. **Database Connection**: Check H2 console accessibility
2. **Port Conflicts**: Ensure port 8080 is available
3. **Memory Issues**: Increase JVM heap size if needed
4. **File Processing**: Verify RTN file format and encoding

### Debug Mode
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## Contributing

### Code Style
- **Java Code Style**: Follow standard Java conventions
- **Naming Conventions**: CamelCase for variables and methods
- **Documentation**: Javadoc for public APIs

### Pull Request Process
1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Ensure all tests pass
5. Submit pull request with detailed description

## License
This project is proprietary software developed for technical assessment purposes.
