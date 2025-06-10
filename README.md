# 🎓 Online Course Platform - Backend

A backend service for an online course platform built using **Spring Boot**, supporting secure user authentication, course management, and enrollment features.

---

## 📌 Features

- ✅ User registration with roles: `STUDENT`, `INSTRUCTOR`
- 🔐 Secure login with **JWT-based authentication**
- 🧾 User profile management
- 📚 Course creation & enrollment
- 💡 OTP-based password reset
- 🚀 Token caching using **Redis**
- 🧪 Unit tested with **JUnit** & **Mockito**

---

## 🧱 Tech Stack

- Java 17
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- MySQL
- Redis
- Lombok
- JUnit 5
- Mockito

---

## 🗂️ Project Structure

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
│   │   ├── utilty/           # Utilities (Converts etc.)
│   │   └── config/           # Configuration
│   └── resources/
│       ├── application.properties
│       └── static/
├── test/                     # Unit Tests
```

---

## ⚙️ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/online-course-platform.git
cd online-course-platform
```

### 2. Set up the MySQL database

- Create a database named `online_course_platform`
- Update the properties file:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/online_course_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### 3. Set up Redis (for token caching)

- Install and run Redis locally (default port: `6379`)

### 4. Run the project

```bash
./mvnw spring-boot:run
```

---

## 🔐 Authentication Flow

- **Login**: Generates a JWT and stores it in Redis.
- **Logout**: Deletes the token from Redis.
- **Token Validity**: Managed automatically using Redis expiry.

---

## 📬 Key APIs

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/register` | POST | Register a new user |
| `/api/auth/login` | POST | Login with credentials |
| `/api/auth/logout` | POST | Logout current user |
| `/api/user/me` | GET | Get current user info |
| `/api/courses` | GET | Get all courses |
| `/api/enroll/{courseId}` | POST | Enroll in a course |

---

## 🧪 Running Tests

Run unit tests using Maven:

```bash
./mvnw test
```

---

## 📄 License

This project is licensed under the **MIT License**.

You are free to use, modify, and distribute this software with attribution.

---

**Author**: Saikrishna G  
**Location**: Hyderabad, Telangana, India  
**Email**: *[you can add your email here if you'd like]*

See the [LICENSE](LICENSE) file for full legal details.

