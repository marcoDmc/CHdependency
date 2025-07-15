# CHdependency API

CHdependency is a Spring Boot API designed to manage user authentication, addictions, and goals, with secure JWT-based authentication. The API provides endpoints for user registration, authentication, addiction tracking, and goal management, using Spring Security, Spring Data JPA, and PostgreSQL. API documentation is automatically generated with Springdoc OpenAPI and can be accessed via Swagger UI.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Environment Setup](#environment-setup)
- [Application Configuration](#application-configuration)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Features

- **User Management**: User creation, password updates, and user deletion
- **Authentication**: JWT-based authentication with RSA key pairs
- **Addiction Tracking**: Creation and deletion of addiction records associated with users
- **Goal Management**: Creation, period detail queries, and deletion of goals
- **API Documentation**: Interactive Swagger UI for exploring and testing endpoints
- **Security**: JWT-protected endpoints, public endpoints for authentication and user creation

## Technologies

- **Java**: 21
- **Spring Boot**: 3.5.3
- **Spring Security**: JWT-based authentication with OAuth2 Resource Server
- **Spring Data JPA**: For database operations
- **PostgreSQL**: Database for storing users, addictions, and goals
- **Springdoc OpenAPI**: For API documentation (Swagger UI)
- **MapStruct**: For DTO to entity mapping
- **Lombok**: For reducing boilerplate code
- **Gradle**: Build tool
- **JUnit**: For testing

## Environment Setup

### Prerequisites

- **Java 21**: Ensure JDK 21 is installed
- **PostgreSQL**: Install and configure a PostgreSQL database
- **Gradle**: Ensure Gradle is installed or use the wrapper (gradlew)

### Installation

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd CHdependency
   ```

2. **Configure the Database**:
   
   Create a PostgreSQL database (e.g., `chdependency_db`).
   
   Update `src/main/resources/application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/chdependency_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Configure JWT Keys**:
   
   Generate an RSA key pair for JWT signing:
   ```bash
   openssl genrsa -out keypair.pem 2048
   openssl rsa -in keypair.pem -pubout -out public.pem
   ```
   
   Add the keys to `application.properties`:
   ```properties
   jwt.public.key=-----BEGIN PUBLIC KEY-----...-----END PUBLIC KEY-----
   jwt.private.key=-----BEGIN PRIVATE KEY-----...-----END PRIVATE KEY-----
   ```

4. **Build the Project**:
   ```bash
   ./gradlew build
   ```

## Application Configuration

### Application Properties

Configure `src/main/resources/application.properties` for database, JWT, and Swagger settings:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chdependency_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

jwt.public.key=your-public-key
jwt.private.key=your-private-key

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
spring.application.name=CHdependency API
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.display-request-duration=true
```

### Security Configuration

- **Public Endpoints**: `/private/authenticate`, `/api/v1/user/create`, `/swagger-ui/**`, `/v3/api-docs/**` are accessible without authentication
- **Protected Endpoints**: All other endpoints require a valid JWT token in the Authorization header (Bearer token)
- **Password Encoding**: Uses BCrypt for secure password hashing
- **JWT**: Tokens are generated with 1-hour expiration and signed with an RSA private key

## API Endpoints

The API is organized into four main controllers, documented via Swagger UI at `http://localhost:8080/swagger-ui.html`.

### User Controller (`/api/v1`)

- **POST `/user/create`**: Creates a new user
  - **Request**: `UserRequestDTO` (email, name, password)
  - **Response**: `UserResponseDTO` (201 on success, 400 on failure)

- **PATCH `/user/password`**: Updates a user's password
  - **Request**: `UserPasswordDTO` (email, password, newPassword)
  - **Response**: 200 on success, 400/401 on failure

- **DELETE `/user/delete`**: Deletes a user
  - **Request**: `UserDeleteDTO` (email, password)
  - **Response**: 200 on success, 400/401 on failure

### Authentication Controller (`/private`)

- **POST `/authenticate`**: Authenticates a user and returns a JWT token
  - **Request**: `CrendentialsUserDTO` (username, password)
  - **Response**: JWT token (200 on success, 400 on failure)

### Addiction Controller (`/api/v1`)

- **POST `/addiction/create`**: Creates a new addiction for a user
  - **Request**: `AddictionDTO` (email, password, type)
  - **Response**: 201 on success, 400/401 on failure

- **DELETE `/addiction/delete`**: Deletes an addiction
  - **Request**: `DeleteAddictionDTO` (email, password)
  - **Response**: 200 on success, 400/401 on failure

### Goal Controller (`/api/v1`)

- **POST `/meta/create`**: Creates a new goal for a user
  - **Request**: `MetaDTO` (email, password, name, time, range)
  - **Response**: 201 on success, 400/401 on failure

- **POST `/meta/g/period`**: Retrieves period details for a goal
  - **Request**: `FindPeriodDTO` (email, password, name)
  - **Response**: JSON with period details (200 on success, 400/401 on failure)

- **DELETE `/meta/delete`**: Deletes a goal
  - **Request**: `DeleteMetaDTO` (email, password, name)
  - **Response**: 200 on success, 400/401 on failure

## Security

- **Authentication**: Uses Spring Security with JWT (OAuth2 Resource Server)
- **Public Access**: `/private/authenticate`, `/api/v1/user/create`, and Swagger endpoints are public
- **Protected Access**: Other endpoints require a JWT token in the Authorization header (e.g., `Bearer <token>`)
- **Password Handling**: Passwords are hashed with BCrypt
- **CSRF**: Disabled for simplicity, suitable for stateless API with JWT

## Running the Application

1. **Start the Application**:
   ```bash
   ./gradlew bootRun
   ```

2. **Access Swagger UI**:
   
   Open `http://localhost:8080/swagger-ui.html` to explore and test the API endpoints.

3. **Test Authentication**:
   
   Use a tool like curl or Postman to authenticate:
   ```bash
   curl -X POST http://localhost:8080/private/authenticate \
     -H "Content-Type: application/json" \
     -d '{"username":"test","password":"test"}'
   ```
   
   Use the returned JWT token to access protected endpoints:
   ```bash
   curl -X POST http://localhost:8080/api/v1/addiction/create \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"test","type":"example"}'
   ```

## Testing

- **Unit Tests**: Use JUnit with spring-boot-starter-test for unit and integration tests
- **Run Tests**:
  ```bash
  ./gradlew test
  ```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m "Add your feature"`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a pull request

## License

This project is licensed under the MIT License.