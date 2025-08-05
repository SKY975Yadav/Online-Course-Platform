# 🎓 Online Course Platform – Backend (Spring Boot)

A production-ready Online Course Management System built using Spring Boot. It supports multi-role access (Student, Instructor, Admin) with secure authentication, course management, payment integration, and Redis-based session handling.

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
- Current user profile management

### 👥 User Management
- Register/Login/Logout endpoints
- Admin initialization on app startup
- Profile update (name, email) and password change APIs
- Role-specific access control
- Admin-only user deletion and user listing

### 📚 Course Management
- **Instructors can:**
  - Create and manage their courses with modular structure
  - Upload multiple videos and documents per module
  - View enrolled students and enrollment counts
  - Update course details, pricing, and content
  - Delete their courses
- **Students can:**
  - Browse and search courses
  - View course details and content (if enrolled)
  - Enroll and unenroll from courses
  - Mark courses as completed
  - Access secure streaming of videos and documents
- **General Features:**
  - Course search functionality
  - Popular courses listing
  - Detailed course information with modules

### 📊 Enrollment System
- Enrollment & unenrollment with timestamp tracking
- Prevent duplicate enrollments
- Course completion tracking with status management
- View enrollment history (for users & admins)
- Enrollment status checking
- Student course dashboard

### 💳 Payment Integration
- Razorpay payment gateway integration
- Create payment orders for course enrollment
- Payment verification and secure transaction handling
- Payment history tracking for users
- Multiple payment status management (PENDING, SUCCESS, FAILED)

### 📝 Feedback & Review System
- Students can submit course reviews and ratings (1-5 stars)
- Review titles and detailed feedback
- View all feedbacks for courses
- Feedback retrieval by ID
- Rating-based course evaluation

### 🎯 Content Management
- **Modular Course Structure:**
  - Courses organized into modules
  - Multiple videos and documents per module
  - Cloud provider integration (Google Drive, Dropbox, Other)
  - Secure content streaming for enrolled students
- **File Management:**
  - Video streaming with access control
  - Document download with permission checks
  - URL-based content delivery

### 🛠 Additional Features
- Swagger/OpenAPI 3.1 documentation
- Redis for session/token management
- Audit logging with timestamps
- Global exception handling
- Input validation with custom DTOs
- Transaction management
- Advanced search capabilities
- Popular course recommendations

---

## 🛠 Tech Stack

| Layer         | Technology         |
|---------------|--------------------|
| Language      | Java 17+           |
| Framework     | Spring Boot 3.x    |
| Security      | Spring Security 6.x, JWT |
| Database      | MySQL              |
| Caching/Session | Redis            |
| Payment       | Razorpay           |
| Testing       | JUnit 5, Mockito   |
| Docs          | Swagger / OpenAPI 3.1 |
| Build Tool    | Maven              |
| Cloud Storage | Google Drive, Dropbox |

---

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/com/example/onlinecourseplatform/
│   │   ├── controller/         # REST APIs
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   ├── CourseController.java
│   │   │   ├── EnrollmentController.java
│   │   │   ├── PaymentController.java
│   │   │   ├── FeedbackController.java
│   │   │   └── SecureCourseContentController.java
│   │   ├── dto/               # DTOs and validation
│   │   │   ├── request/       # Request DTOs
│   │   │   └── response/      # Response DTOs
│   │   ├── entity/            # JPA Entities
│   │   │   ├── User.java
│   │   │   ├── Course.java
│   │   │   ├── Module.java
│   │   │   ├── Video.java
│   │   │   ├── Document.java
│   │   │   ├── Enrollment.java
│   │   │   ├── Payment.java
│   │   │   └── Feedback.java
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
- Razorpay Account (for payments)
- Docker (optional)

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
# Docker
docker run -d --name redis -p 6379:6379 redis

# Or install manually
redis-server
```

4. **Set Environment Variables**

```bash
export SECRET_KEY=your-jwt-secret
export JAVA_PROJECTS_ADMIN_EMAIL=admin@example.com
export JAVA_PROJECTS_ADMIN_PASSWORD=admin123
export RAZORPAY_KEY_ID=your-razorpay-key-id
export RAZORPAY_KEY_SECRET=your-razorpay-key-secret
```

5. **Update application.properties**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/online_course_platform
spring.datasource.username=your-username
spring.datasource.password=your-password

# Razorpay Configuration
razorpay.key.id=${RAZORPAY_KEY_ID}
razorpay.key.secret=${RAZORPAY_KEY_SECRET}
```

6. **Run the App**

```bash
mvn clean install
mvn spring-boot:run
```

The application will be live at: **http://localhost:8080**
API Documentation: **http://localhost:8080/swagger-ui.html**

---

## 📖 API Endpoints

### 🔐 Authentication
- `POST /api/auth/register` - Register new user (Student/Instructor)
- `POST /api/auth/login` - User login with JWT
- `POST /api/auth/logout` - User logout
- `POST /api/auth/forgot-password` - Request password reset OTP
- `POST /api/auth/verify-otp` - Verify OTP for password reset
- `POST /api/auth/reset-password` - Reset password with verified OTP

### 👤 User Management
- `GET /api/users/me` - Get current user profile
- `PUT /api/users/me` - Update current user profile
- `PUT /api/users/change-password` - Change current user password
- `GET /api/users/all` - Get all users (Admin only)
- `DELETE /api/users/{id}` - Delete user by ID (Admin only)

### 📘 Course Management
- `GET /api/courses/all` - Get all courses (basic details)
- `GET /api/courses/popular` - Get popular courses with limit
- `GET /api/courses/search` - Search courses by query
- `GET /api/courses/{id}` - Get course details by ID
- `POST /api/courses/create` - Create new course (Instructor only)
- `PUT /api/courses/{id}` - Update course (Owner only)
- `DELETE /api/courses/{id}` - Delete course (Owner/Admin only)
- `GET /api/courses/instructor` - Get instructor's courses
- `GET /api/courses/{id}/students` - Get enrolled students (Owner only)
- `GET /api/courses/{id}/students-count` - Get enrollment count
- `GET /api/courses/{id}/course-content` - Get course content (Enrolled students)

### 📌 Enrollment Management
- `GET /api/enrollments` - Get current student's enrollments
- `GET /api/enrollments/courses` - Get enrolled courses
- `POST /api/enrollments/enroll/{courseId}` - Enroll in course
- `GET /api/enrollments/{courseId}/is-enrolled` - Check enrollment status
- `PUT /api/enrollments/completed/{courseId}` - Mark course as completed
- `GET /api/enrollments/all` - Get all enrollments (Admin only)

### 💳 Payment System
- `POST /api/payment/create-order` - Create Razorpay order
- `POST /api/payment/verify` - Verify payment and complete enrollment
- `GET /api/payment/my` - Get user's payment history

### 📝 Feedback & Reviews
- `POST /api/courses/feedbacks/course/{id}` - Submit course feedback
- `GET /api/courses/feedbacks/course/{id}` - Get all course feedbacks
- `GET /api/courses/feedbacks/{id}` - Get feedback by ID

### 🔒 Secure Content Access
- `GET /api/secure/content/video/{videoId}` - Stream video (Enrolled students)
- `GET /api/secure/content/document/{documentId}` - Download document (Enrolled students)

---

## 🗄 Database Schema

```sql
-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'INSTRUCTOR', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Courses Table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    instructor_id BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(id)
);

-- Modules Table
CREATE TABLE modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Videos Table
CREATE TABLE videos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    description TEXT,
    cloud_provider ENUM('GOOGLE_DRIVE', 'DROPBOX', 'OTHER') DEFAULT 'OTHER',
    url TEXT,
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
);

-- Documents Table
CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    cloud_provider ENUM('GOOGLE_DRIVE', 'DROPBOX', 'OTHER') DEFAULT 'OTHER',
    url TEXT,
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
);

-- Enrollments Table
CREATE TABLE enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    price DECIMAL(10, 2),
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    status ENUM('ACTIVE', 'COMPLETED') DEFAULT 'ACTIVE',
    UNIQUE KEY unique_enrollment (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Payments Table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    order_id VARCHAR(255),
    payment_id VARCHAR(255),
    signature VARCHAR(255),
    amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    payment_method VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Feedbacks Table
CREATE TABLE feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    review_title VARCHAR(100),
    review TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_feedback (course_id, student_id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (student_id) REFERENCES users(id)
);
```

---

## 🧪 Testing

Includes comprehensive unit tests for:
- AuthService (Login, Registration, OTP verification)
- UserService (Profile management, password changes)
- CourseService (CRUD operations, search functionality)
- EnrollmentService (Enrollment management, completion tracking)
- PaymentService (Order creation, payment verification)
- FeedbackService (Review management)
- RedisService (Session management)

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
| RAZORPAY_KEY_ID | Razorpay Key ID | rzp_test_xxxxx |
| RAZORPAY_KEY_SECRET | Razorpay Key Secret | your_secret_key |

---

## 🔒 Security

✅ JWT-based Authentication  
✅ BCrypt Password Encoding  
✅ Role-based Access Control (RBAC)  
✅ Redis Token Storage & Management  
✅ OTP-based Password Reset  
✅ Input Validation & Sanitization  
✅ Global Exception Handling  
✅ Secure Content Streaming  
✅ Payment Security with Razorpay  
✅ CORS Configuration  

---

## 🚀 Deployment

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/online-course-platform.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/online_course_platform
      - SPRING_REDIS_HOST=redis
    depends_on:
      - db
      - redis
      
  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: online_course_platform
      MYSQL_ROOT_PASSWORD: rootpassword
    ports:
      - "3306:3306"
      
  redis:
    image: redis:alpine
    ports:
      - "6379:6379"
```

### Notes for Production
- Use managed MySQL & Redis (e.g., AWS RDS, ElastiCache)
- Enable HTTPS with SSL certificates
- Configure rate limiting and API throttling
- Set up comprehensive logging & monitoring
- Implement backup & disaster recovery
- Use CDN for static content delivery
- Configure proper CORS policies
- Set up health checks and monitoring

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
- Razorpay payment gateway
- JWT.io documentation
- OpenAPI/Swagger documentation
- Java and Open Source developers ✨

---

## 📊 API Statistics

- **Total Endpoints:** 35+
- **Authentication Endpoints:** 6
- **User Management:** 5
- **Course Management:** 10
- **Enrollment System:** 6
- **Payment Integration:** 3
- **Feedback System:** 3
- **Content Security:** 2

---

⭐ **If this project helped you, leave a ⭐ on GitHub to show your support!**
