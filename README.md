# 🎓 Online Course Platform – Backend (Spring Boot)

A production-ready Online Course Management System built using Spring Boot. It supports multi-role access (Student, Instructor, Admin) with secure authentication, course management,Razorpay Payment Integration, and Redis-based session handling.

---

## 📚 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)
- [Testing](#-testing)
- [Environment Variables](#-environment-variables)
- [Security](#-security)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)
- [Author](#-author)
- [Acknowledgments](#-acknowledgments)

---

## ✨ Features

### 🔐 Authentication & Authorization
- JWT-based stateless login
- Role-based access: `STUDENT`, `INSTRUCTOR`, `ADMIN`
- Redis-powered session/token management
- Password reset via OTP verification
- Secure password hashing (BCrypt)

### 👥 User Management
- Register/Login/Logout endpoints
- Admin initialization on app startup
- Profile update and password change APIs
- Role-specific access control

### 📚 Course Management
- Instructors can:
  - Create and manage their courses
  - Upload 1 video and 1 document per course
  - View enrolled students
- Students can:
  - Browse, enroll, and unenroll from courses
  - View course details

### 📊 Enrollment System
- Enrollment & unenrollment with timestamp
- Prevent duplicate enrollments
- View enrollment history (for users & admins)

### 🛠 Additional Features
- Swagger/OpenAPI documentation
- Redis for session/token management
- Audit logging (optional)
- Global exception handling
- Input validation with custom DTOs
- Transaction management
- Razorpay Payment Integration

---

## 🛠 Tech Stack

| Layer         | Technology         |
|---------------|--------------------|
| Language      | Java 17+           |
| Framework     | Spring Boot 3.x    |
| Security      | Spring Security 6.x, JWT |
| Database      | MySQL              |
| Caching/Session | Redis            |
| Testing       | JUnit 5, Mockito   |
| Docs          | Swagger / OpenAPI 3 |
| Build Tool    | Maven              |

---

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/com/example/onlinecourseplatform/       
│   │   ├── controller/         # REST APIs
│   │   ├── dto/               # DTOs and validation
│   │   ├── entity/            # JPA Entities
│   │   ├── repository/        # DB layer
│   │   ├── security/          # JWT, filters, config
│   │   ├── service/           # Business logic
│   │   ├── utility/           # Mappers, helpers
│   │   └── config/            # App config
│   └── resources/
│       ├── application.properties
│       └── static/
└── test/                      # Unit tests
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8+
- Redis 6+

### Setup

1. **Clone the repo**

```bash
git clone https://github.com/SKY975Yadav/Online-Course-Platform.git
cd Online-Course-Platform
```

2. **Create MySQL Database**

```sql
CREATE DATABASE online_course_platform;
```

3. **Start Redis**

```bash

# Install manually
redis-server
```

4. **Set Environment Variables**

```bash
export SECRET_KEY=your-jwt-secret
export JAVA_PROJECTS_ADMIN_EMAIL=admin@example.com or create your own
export JAVA_PROJECTS_ADMIN_PASSWORD=admin123 or create your own
```

5. **Update application.properties**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/online_course_platform
spring.datasource.username=your-username
spring.datasource.password=your-password
```

6. **Run the App**

```bash
mvn clean install
mvn spring-boot:run
```

The application will be live at: **http://localhost:8080**

---

## 📖 API Endpoints

### 🔐 Auth
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/logout`
- `POST /auth/forgot-password`
- `POST /auth/reset-password`

### 👤 Users
- `GET /api/users` – Admin only
- `PUT /api/users/update`
- `POST /api/users/change-password`
- `DELETE /api/users/{id}` – Admin only

### 📘 Courses
- `GET /api/courses`
- `POST /api/courses` – Instructor only
- `GET /api/courses/{id}`
- `PUT /api/courses/{id}` – Owner only
- `DELETE /api/courses/{id}` – Owner only
- `GET /api/courses/instructor/{id}`
- `GET /api/courses/{id}/students` – Owner only

### 📌 Enrollments
- `GET /api/enrollments`
- `POST /api/enrollments/{courseId}`
- `DELETE /api/enrollments/{courseId}`
- `GET /api/enrollments/all` – Admin only

---

## 🗄 Database Schema

```sql
-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    role ENUM('STUDENT', 'INSTRUCTOR', 'ADMIN') NOT NULL
);

-- Courses Table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    instructor_id BIGINT,
    price DECIMAL(10, 2),
    FOREIGN KEY (instructor_id) REFERENCES users(id)
);

-- Enrollments Table
CREATE TABLE enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    course_id BIGINT,
    enrolled_at DATETIME,
    UNIQUE KEY unique_enrollment (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);
```

---

## 🧪 Testing

Includes unit tests for:
- AuthService
- UserService
- CourseService
- EnrollmentService
- RedisService

### Run Tests
```bash
mvn test
# Or for coverage
mvn test jacoco:report
```

---

## 🔧 Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| SECRET_KEY | JWT Signing Key | mySecretKey123 |
| JAVA_PROJECTS_ADMIN_EMAIL | Default admin email | admin@example.com |
| JAVA_PROJECTS_ADMIN_PASSWORD | Default admin password | admin123 |

---

## 🔒 Security

✅ JWT-based Authentication  
✅ BCrypt Password Encoding  
✅ Role-based Access Control  
✅ Redis Token Storage  
✅ OTP Password Reset  
✅ Input Validation  
✅ Global Exception Handling  

---

## 🚀 Deployment

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/online-course-platform.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Notes for Production
- Use managed MySQL & Redis (e.g., AWS RDS, ElastiCache)
- Enable HTTPS with SSL certs
- Configure rate limiting
- Add logging & monitoring
- Set up backup & recovery

---

## 🤝 Contributing

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add Feature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Create a pull request 🎉

---

## 📝 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**Saikrishna G**
- GitHub: [@SKY975Yadav](https://github.com/SKY975Yadav)
- Project Link: [Online Course Platform](https://github.com/SKY975Yadav/Online-Course-Platform)

---

## 🙏 Acknowledgments

- Spring Boot community
- Redis contributors
- JWT.io documentation
- Java and Open Source developers ✨

---

⭐ **If this project helped you, leave a ⭐ on GitHub to show your support!**
