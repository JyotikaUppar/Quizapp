package com.FirstSpring.quizproject.config;

import com.FirstSpring.quizproject.Question;
import com.FirstSpring.quizproject.dao.QuestionDao;
import com.FirstSpring.quizproject.dao.UserDao;
import com.FirstSpring.quizproject.model.Role;
import com.FirstSpring.quizproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Initializes default Admin & Student credentials and baseline questions
 * on first startup if database is fresh.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Seed Default Demo Users if missing
        if (!userDao.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Admin / Teacher")
                    .role(Role.ROLE_ADMIN)
                    .build();
            userDao.save(admin);
        }

        if (!userDao.existsByUsername("student")) {
            User student = User.builder()
                    .username("student")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Candidate / Student")
                    .role(Role.ROLE_STUDENT)
                    .build();
            userDao.save(student);
        }

        // 2. Seed Baseline Questions if question bank is empty
        if (questionDao.count() == 0) {
            Question q1 = new Question();
            q1.setQuestionTitle("Which Java 8 feature enables functional programming and concise syntax?");
            q1.setOption1("Lambda Expressions");
            q1.setOption2("Applets");
            q1.setOption3("AWT Components");
            q1.setOption4("Coroutines");
            q1.setRightAnswer("Lambda Expressions");
            q1.setCategory("Java");
            q1.setDifficultyLevel("Easy");

            Question q2 = new Question();
            q2.setQuestionTitle("Which container class in java.util is designed to prevent NullPointerException?");
            q2.setOption1("Optional");
            q2.setOption2("Nullable");
            q2.setOption3("SafeReference");
            q2.setOption4("Wrapper");
            q2.setRightAnswer("Optional");
            q2.setCategory("Java");
            q2.setDifficultyLevel("Medium");

            Question q3 = new Question();
            q3.setQuestionTitle("Which Stream method is an intermediate operation in Java 8?");
            q3.setOption1("filter");
            q3.setOption2("collect");
            q3.setOption3("forEach");
            q3.setOption4("count");
            q3.setRightAnswer("filter");
            q3.setCategory("Java");
            q3.setDifficultyLevel("Easy");

            Question q4 = new Question();
            q4.setQuestionTitle("What Spring Boot annotation marks the main entry point class?");
            q4.setOption1("@SpringBootApplication");
            q4.setOption2("@EnableAutoConfiguration");
            q4.setOption3("@Configuration");
            q4.setOption4("@Component");
            q4.setRightAnswer("@SpringBootApplication");
            q4.setCategory("SpringBoot");
            q4.setDifficultyLevel("Easy");

            Question q5 = new Question();
            q5.setQuestionTitle("Which Spring Boot starter provides health, metrics, and application monitoring?");
            q5.setOption1("spring-boot-starter-actuator");
            q5.setOption2("spring-boot-starter-web");
            q5.setOption3("spring-boot-starter-metrics");
            q5.setOption4("spring-boot-starter-monitor");
            q5.setRightAnswer("spring-boot-starter-actuator");
            q5.setCategory("SpringBoot");
            q5.setDifficultyLevel("Medium");

            questionDao.saveAll(Arrays.asList(q1, q2, q3, q4, q5));
        }
    }
}
