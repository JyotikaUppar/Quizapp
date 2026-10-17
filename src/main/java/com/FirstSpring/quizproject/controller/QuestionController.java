package com.FirstSpring.quizproject.controller;

import com.FirstSpring.quizproject.Question;
import com.FirstSpring.quizproject.dto.QuestionWrapper;
import com.FirstSpring.quizproject.dto.QuizAnalyticsDto;
import com.FirstSpring.quizproject.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("question")
@CrossOrigin(origins = "*")
@Tag(name = "Question API", description = "CRUD operations, Java 8 Stream analytics, and filtering for quiz questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // ── GET All (paginated) ───────────────────────────────────────────────────
    @Operation(summary = "Get all questions with pagination")
    @GetMapping("allQuestion")
    public ResponseEntity<Page<Question>> getAllQuestion(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")             @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")            @RequestParam(defaultValue = "id") String sortBy) {
        return questionService.getAllQuestions(page, size, sortBy);
    }

    // ── GET All Safe Wrappers (Java 8 Streams) ────────────────────────────────
    @Operation(summary = "Get all questions as safe QuestionWrapper DTOs (without correct answer)")
    @GetMapping("wrappers")
    public ResponseEntity<List<QuestionWrapper>> getAllQuestionWrappers() {
        return questionService.getAllQuestionWrappers();
    }

    // ── GET By ID (Java 8 Optional) ───────────────────────────────────────────
    @Operation(summary = "Get a question by its ID")
    @GetMapping("{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Integer id) {
        return questionService.getQuestionById(id);
    }

    // ── GET Active Options (Java 8 Streams & Optional) ────────────────────────
    @Operation(summary = "Get non-null, active answer options for a question using Java 8 Optional + Streams")
    @GetMapping("{id}/options")
    public ResponseEntity<List<String>> getActiveOptions(@PathVariable Integer id) {
        return questionService.getActiveOptions(id);
    }

    // ── GET By Category ───────────────────────────────────────────────────────
    @Operation(summary = "Get questions by category")
    @GetMapping("category/{category}")
    public ResponseEntity<List<Question>> getQuestionByCategory(@PathVariable String category) {
        return questionService.getQuestionByCategory(category);
    }

    // ── GET By Difficulty ─────────────────────────────────────────────────────
    @Operation(summary = "Get questions by difficulty level")
    @GetMapping("difficulty/{level}")
    public ResponseEntity<List<Question>> getQuestionsByDifficulty(@PathVariable String level) {
        return questionService.getQuestionsByDifficulty(level);
    }

    // ── GET By Category + Difficulty ─────────────────────────────────────────
    @Operation(summary = "Get questions filtered by both category and difficulty")
    @GetMapping("filter")
    public ResponseEntity<List<Question>> getByFilter(
            @RequestParam String category,
            @RequestParam String difficulty) {
        return questionService.getQuestionsByCategoryAndDifficulty(category, difficulty);
    }

    // ── GET Search (Java 8 Predicate Lambdas & Streams) ───────────────────────
    @Operation(summary = "Dynamic multi-criteria search using Java 8 Streams and Predicate Lambdas")
    @GetMapping("search")
    public ResponseEntity<List<Question>> searchQuestions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        return questionService.searchQuestions(keyword, category, difficulty, sortBy, sortDirection);
    }

    // ── GET Categories List (Java 8 Streams distinct) ─────────────────────────
    @Operation(summary = "Get all unique categories sorted alphabetically using Java 8 Streams")
    @GetMapping("categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return questionService.getAllCategories();
    }

    // ── GET Analytics (Java 8 Stream groupingBy & counting) ───────────────────
    @Operation(summary = "Get quiz question statistics calculated via Java 8 Collectors.groupingBy and counting")
    @GetMapping("analytics")
    public ResponseEntity<QuizAnalyticsDto> getQuizAnalytics() {
        return questionService.getQuizAnalytics();
    }

    // ── GET Grouped by Category (Java 8 Collectors.mapping) ───────────────────
    @Operation(summary = "Get all questions grouped by category using Java 8 Collectors.groupingBy and mapping")
    @GetMapping("grouped")
    public ResponseEntity<Map<String, List<QuestionWrapper>>> getQuestionsGroupedByCategory() {
        return questionService.getQuestionsGroupedByCategory();
    }

    // ── GET Random Sample (Java 8 Collections.shuffle & Streams) ──────────────
    @Operation(summary = "Get a random sample of questions using Java 8 Collections.shuffle and Streams")
    @GetMapping("random")
    public ResponseEntity<List<QuestionWrapper>> getRandomQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "5") int count) {
        return questionService.getRandomQuestions(category, count);
    }

    // ── POST Add ──────────────────────────────────────────────────────────────
    @Operation(summary = "Add a new question")
    @PostMapping("add")
    public ResponseEntity<String> addQuestion(@Valid @RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    // ── PUT Update (Java 8 Optional) ──────────────────────────────────────────
    @Operation(summary = "Update an existing question by ID")
    @PutMapping("update/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Integer id,
            @Valid @RequestBody Question question) {
        return questionService.updateQuestion(id, question);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Operation(summary = "Delete a question by ID")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Integer id) {
        return questionService.deleteQuestion(id);
    }
}
