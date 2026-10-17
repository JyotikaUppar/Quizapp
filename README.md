# 🧠 Quiz Manager — Spring Boot REST API

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.7-6DB33F?style=flat&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17%20LTS%20(Java%208%20features)-ED8B00?style=flat&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-336791?style=flat&logo=postgresql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=flat&logo=swagger&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat)

A full-featured **Quiz Management REST API** built with Spring Boot, featuring a built-in frontend dashboard, Swagger UI, Spring Actuator, pagination, full CRUD, and global exception handling.

---

## 📸 Preview

> Open `http://localhost:8081` after running the app to see the dashboard.

| Feature | URL |
|---------|-----|
| 🖥️ Frontend Dashboard | `http://localhost:8081` |
| 📄 Swagger UI | `http://localhost:8081/swagger-ui.html` |
| 🩺 Actuator Health | `http://localhost:8081/actuator/health` |
| ℹ️ App Info | `http://localhost:8081/actuator/info` |
| 📊 Metrics | `http://localhost:8081/actuator/metrics` |
| 📋 OpenAPI JSON | `http://localhost:8081/api-docs` |

---

## ✨ Features

- 🔐 **Spring Security & JWT RBAC** — Stateless JWT token authentication with granular Role-Based Access Control
  - 👑 **Admin / Teacher**: Full CRUD access on questions, quiz builder, delete quizzes, and platform analytics
  - 🎓 **Candidate / Student**: Read-only browsing, interactive quiz taking, instant scorecards, and answer review
- 🎮 **Interactive Quiz Arena** — Fullscreen player mode with live countdown timer, audio cues, keyboard navigation, and question jumper palette
- 🎯 **Quiz Hub & Custom Builder** — Generate quizzes by category, size, and scoring mode (Standard, Negative Marking, Percentage)
- 🎲 **Instant Random Challenge** — 1-click randomized quiz generation and immediate launch
- 🏆 **Detailed Results & Review** — Confetti celebration, performance remarks, score percentage, and side-by-side answer breakdown
- 📊 **Visual Analytics** — Live breakdown of questions by category and difficulty
- ✅ **Full Question Bank CRUD** — Create, Read, Update, Delete questions with live search & filtering
- 📄 **Pagination & Sorting** — Server-side paginated responses
- ✅ **Bean Validation** — `@NotBlank`, `@Size` on all required fields
- ⚠️ **Global Exception Handling** — Structured JSON errors (400, 401, 403, 404, 500)
- 📄 **Swagger / OpenAPI 3** — Interactive API documentation with Bearer Auth
- 🩺 **Spring Boot Actuator** — Health, info, and metrics endpoints
- 🖥️ **Built-in Frontend Dashboard** — Glassmorphic dark-themed SPA (no Node build step needed)
- 🗄️ **PostgreSQL** — Persistent data storage via Spring Data JPA

---

## 👥 Default Demo Credentials

| Role | Username | Password | Permissions |
|------|----------|----------|-------------|
| 👑 **Admin / Teacher** | `admin` | `admin123` | Full CRUD on questions, create/delete quizzes, inspect analytics |
| 🎓 **Candidate / Student** | `student` | `student123` | Read-only browsing, take quizzes, submit answers, view scores |

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 4.0.7 |
| Security | Spring Security 7 + JWT (JJWT 0.12.6) + BCrypt |
| Language | Java 17 LTS (Java 8 features: Streams, Lambdas, Optional) |
| Database | PostgreSQL 18 |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Monitoring | Spring Boot Actuator |
| Utilities | Lombok |
| Build | Maven (Maven Wrapper) |

---

## 📁 Project Structure

```
quizproject/
├── src/
│   ├── main/
│   │   ├── java/com/FirstSpring/quizproject/
│   │   │   ├── QuizappApplication.java          # Entry point
│   │   │   ├── Question.java                    # JPA Entity
│   │   │   ├── controller/
│   │   │   │   └── QuestionController.java      # REST Controller
│   │   │   ├── service/
│   │   │   │   └── QuestionService.java         # Business Logic
│   │   │   ├── dao/
│   │   │   │   └── QuestionDao.java             # JPA Repository
│   │   │   └── exception/
│   │   │       ├── ResourceNotFoundException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html                   # Frontend Dashboard
│   └── test/
│       └── java/com/FirstSpring/quizproject/
│           └── QuizprojectApplicationTests.java
└── pom.xml
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+ (project uses Java 25)
- Maven 3.6+ (or use included `mvnw`)
- PostgreSQL running locally

### 1. Clone the repository

```bash
git clone https://github.com/JyotikaUppar/SpringProject.git
cd SpringProject
```

### 2. Configure the database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/questionall
spring.datasource.username=postgres
spring.datasource.password=your_password
```

Create the PostgreSQL database if it doesn't exist:

```sql
CREATE DATABASE questionall;
```

### 3. Run the application

```bash
./mvnw spring-boot:run        # Linux / macOS
.\mvnw.cmd spring-boot:run    # Windows
```

The app starts on **http://localhost:8081**

---

## 🔗 API Reference

Base URL: `http://localhost:8081/question`

### Question Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/question/allQuestion?page=0&size=10&sortBy=id` | Get all questions (paginated) |
| `GET` | `/question/{id}` | Get question by ID |
| `GET` | `/question/category/{category}` | Filter by category |
| `GET` | `/question/difficulty/{level}` | Filter by difficulty (Easy/Medium/Hard) |
| `GET` | `/question/filter?category=X&difficulty=Y` | Filter by category + difficulty |
| `POST` | `/question/add` | Add a new question |
| `PUT` | `/question/update/{id}` | Update an existing question |
| `DELETE` | `/question/delete/{id}` | Delete a question |

### Sample Request — Add Question

```http
POST /question/add
Content-Type: application/json

{
  "questionTitle": "What is the capital of France?",
  "option1": "Paris",
  "option2": "London",
  "option3": "Berlin",
  "option4": "Rome",
  "rightAnswer": "Paris",
  "difficultyLevel": "Easy",
  "category": "Geography"
}
```

### Sample Response — Validation Error (400)

```json
{
  "timestamp": "2026-09-22T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "fieldErrors": {
    "questionTitle": "Question title must not be blank",
    "rightAnswer": "Right answer must not be blank"
  }
}
```

### Sample Response — Not Found (404)

```json
{
  "timestamp": "2026-09-22T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Question not found with id: 99"
}
```

---

## 🩺 Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /actuator/health` | App + DB health status |
| `GET /actuator/info` | App name, version, description |
| `GET /actuator/metrics` | JVM, HTTP, DB metrics |

---

## 🗄️ Database Schema

The `question` table is auto-created by Hibernate (`ddl-auto=update`):

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `INTEGER` | Primary Key, Auto-increment |
| `question_title` | `VARCHAR` | Not Null |
| `option1` | `VARCHAR` | Not Null |
| `option2` | `VARCHAR` | Not Null |
| `option3` | `VARCHAR` | Nullable |
| `option4` | `VARCHAR` | Nullable |
| `right_answer` | `VARCHAR` | Not Null |
| `difficulty_level` | `VARCHAR` | Not Null (`Easy` / `Medium` / `Hard`) |
| `category` | `VARCHAR` | Not Null |

---

## ⚙️ Configuration Reference

```properties
# Server
server.port=8081

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/questionall
spring.datasource.username=postgres
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
info.app.name=Quiz Application
info.app.version=1.0.0

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 📬 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 👩‍💻 Author

**Jyotika Uppar**  
GitHub: [@JyotikaUppar](https://github.com/JyotikaUppar)

---

## 📄 License

This project is licensed under the MIT License.
