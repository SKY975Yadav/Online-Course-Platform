# Online Course Platform 🎓 (In Proggress)

A comprehensive Spring Boot application for managing online courses with role-based access control, JWT authentication, and Redis session management.

## 📋 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Testing](#-testing)
- [Environment Variables](#-environment-variables)
- [Contributing](#-contributing)

## ✨ Features

### 🔐 Authentication & Authorization
- JWT-based authentication
- Role-based access control (STUDENT, INSTRUCTOR, ADMIN)
- Redis-powered session management
- Password reset with OTP verification
- Secure password encoding

### 👥 User Management
- User registration and profile management
- Role-specific dashboards
- Password change functionality
- Admin user initialization

### 📚 Course Management
- Create, read, update, delete courses
- Instructor-specific course management
- Course enrollment system

### 📊 Enrollment System
- Course enrollment/unenrollment
- Enrollment history tracking
- Instructor view of enrolled students
- Automatic cleanup on user/course deletion

### 🔧 Additional Features
- Comprehensive input validation
- Global exception handling
- Swagger API documentation
- Transaction management
- Audit logging

## 🛠 Tech Stack

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security 6.x**
- **Spring Data JPA**
- **JWT (JSON Web Tokens)**
- **Redis** - Session management
- **MySQL** - Primary database
- **Maven** - Dependency management

### Testing
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**

### Documentation
- **Swagger/OpenAPI 3**

## 🏗 Architecture

```
src/
├── main/
│   ├── java/com/example/onlinecourseplatform/
│   │   ├── controller/       # REST Controllers
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # JPA Entities
│   │   ├── repository/       # Data Access Layer
│   │   ├── security/         # JWT Config & Filters
│   │   ├── service/          # Business Logic
│   │   ├── utilty/           # Utilities (Mappers etc.)
│   │   └── config/           # Security Configuration
│   └── resources/
│       ├── application.properties
│       └── static/
├── test/                     # Unit Tests
```

### Key Design Patterns
- **MVC Pattern** - Clear separation of concerns
- **Repository Pattern** - Data access abstraction
- **DTO Pattern** - Data transfer and validation
- **Builder Pattern** - Object construction
- **Dependency Injection** - Loose coupling

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker (optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/SKY975Yadav/Online-Course-Platform.git
   cd Online-Course-Platform
   ```

2. **Set up MySQL Database**
   ```sql
   CREATE DATABASE online_course_platform;
   ```

3. **Start Redis Server**
   ```bash
   # Using Docker
   docker run -d --name my-redis -p 6379:6379 redis:latest
   
   # Or install locally and start
   redis-server
   ```

4. **Configure Environment Variables**
   ```bash
   export SECRET_KEY=your-jwt-secret-key-here
   export JAVA_PROJECTS_ADMIN_EMAIL=admin@example.com
   export JAVA_PROJECTS_ADMIN_PASSWORD=admin123
   ```

5. **Update application.properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/online_course_platform
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

6. **Run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The application will start at `http://localhost:8080`

## 📖 API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

### Key Endpoints

#### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/logout` - User logout
- `POST /auth/forgot-password` - Initiate password reset
- `POST /auth/reset-password` - Reset password with OTP

#### Users
- `GET /api/users` - Get all users (Admin only)
- `PUT /api/users/update` - Update user profile
- `POST /api/users/change-password` - Change password
- `DELETE /api/users/{id}` - Delete user (Admin only)

#### Courses
- `GET /api/courses` - Get all courses
- `POST /api/courses` - Create course (Instructor only)
- `GET /api/courses/{id}` - Get course details
- `PUT /api/courses/{id}` - Update course (Owner only)
- `DELETE /api/courses/{id}` - Delete course (Owner only)
- `GET /api/courses/instructor/{id}` - Get courses by instructor
- `GET /api/courses/{id}/students` - Get enrolled students (Owner only)

#### Enrollments
- `GET /api/enrollments` - Get user's enrollments
- `POST /api/enrollments/{courseId}` - Enroll in course
- `DELETE /api/enrollments/{courseId}` - Unenroll from course
- `GET /api/enrollments/all` - Get all enrollments (Admin only)

## 🗄 Database Schema

### Users Table
Spring boot can generate all these table when you run the appication for the first time or you can create by yourself
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'INSTRUCTOR', 'ADMIN') NOT NULL
);
```

### Courses Table
```sql
CREATE TABLE courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    instructor_id BIGINT NOT NULL,
    price DECIMAL(10,2),
    FOREIGN KEY (instructor_id) REFERENCES users(id)
);
```

### Enrollments Table
```sql
CREATE TABLE enrollments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrolled_at DATETIME NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE KEY unique_enrollment (student_id, course_id)
);
```

## 🧪 Testing

The project includes comprehensive unit tests for all service layers:

- `AuthServiceTest.java` - Authentication service tests
- `CourseServiceTest.java` - Course management tests
- `EnrollmentServiceTest.java` - Enrollment service tests
- `RedisServiceTest.java` - Redis operations tests
- `UserServiceTest.java` - User management tests

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run tests with coverage
mvn test jacoco:report
```

## 🔧 Environment Variables

Required environment variables:

| Variable | Description | Example |
|----------|-------------|---------|
| `SECRET_KEY` | JWT signing key | `mySecretKey123!@#` |
| `JAVA_PROJECTS_ADMIN_EMAIL` | Admin user email | `admin@example.com` |
| `JAVA_PROJECTS_ADMIN_PASSWORD` | Admin user password | `AdminPass123!` |

## 🔒 Security Features

- **JWT Authentication**: Stateless authentication with configurable expiration
- **Password Encryption**: BCrypt hashing for secure password storage
- **Role-based Authorization**: Method-level security based on user roles
- **CORS Configuration**: Cross-origin resource sharing setup
- **Input Validation**: Comprehensive request validation
- **Session Management**: Redis-based token storage for scalability

## 🚀 Deployment

### Docker Deployment (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/online-course-platform.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Considerations
- Configure production database
- Set up Redis cluster for high availability
- Configure HTTPS/SSL
- Set up monitoring and logging
- Configure backup strategies

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Saikrishna G**
- GitHub: [@SKY975Yadav](https://github.com/SKY975Yadav)
- Project Link: [https://github.com/SKY975Yadav/Online-Course-Platform](https://github.com/SKY975Yadav/Online-Course-Platform)

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- JWT.io for JWT implementation guidance
- Redis community for caching solutions

---

⭐ If you found this project helpful, please give it a star!
