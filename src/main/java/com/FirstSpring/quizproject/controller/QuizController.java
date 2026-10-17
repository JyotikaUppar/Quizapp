package com.FirstSpring.quizproject.controller;

import com.FirstSpring.quizproject.Quiz;
import com.FirstSpring.quizproject.dto.QuestionWrapper;
import com.FirstSpring.quizproject.dto.QuizResultDto;
import com.FirstSpring.quizproject.dto.Response;
import com.FirstSpring.quizproject.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
@CrossOrigin(origins = "*")
@Tag(name = "Quiz API", description = "Endpoints for creating quizzes, retrieving questions, and evaluating submissions using Java 8 features")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // ── Create Quiz ───────────────────────────────────────────────────────────
    @Operation(summary = "Create a new quiz from questions matching a category")
    @PostMapping("create")
    public ResponseEntity<String> createQuiz(
            @Parameter(description = "Question category (e.g., Java, Python, GK)") @RequestParam String category,
            @Parameter(description = "Number of questions to pick randomly")        @RequestParam(defaultValue = "5") int numQ,
            @Parameter(description = "Optional custom title for the quiz")         @RequestParam(required = false) String title) {
        return quizService.createQuiz(category, numQ, title);
    }

    // ── Get Quiz Questions (without answers) ──────────────────────────────────
    @Operation(summary = "Get questions for a specific quiz (safe QuestionWrapper DTO without answers)")
    @GetMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id) {
        return quizService.getQuizQuestions(id);
    }

    // ── Submit Quiz & Calculate Result ────────────────────────────────────────
    @Operation(summary = "Submit user answers for a quiz and calculate result using Java 8 Streams and Lambdas")
    @PostMapping("submit/{id}")
    public ResponseEntity<QuizResultDto> submitQuiz(
            @PathVariable Integer id,
            @RequestBody @Valid List<Response> responses,
            @Parameter(description = "Scoring Strategy: STANDARD, NEGATIVE, or PERCENTAGE")
            @RequestParam(defaultValue = "STANDARD") String scoringType) {
        return quizService.submitQuiz(id, responses, scoringType);
    }

    // ── Get All Quizzes ───────────────────────────────────────────────────────
    @Operation(summary = "Get list of all created quizzes")
    @GetMapping("all")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    // ── Delete Quiz ───────────────────────────────────────────────────────────
    @Operation(summary = "Delete a quiz by its ID")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Integer id) {
        return quizService.deleteQuiz(id);
    }
}
