package com.FirstSpring.quizproject.controller;

import com.FirstSpring.quizproject.Question;
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

@RestController
@RequestMapping("question")
@CrossOrigin(origins = "*")
@Tag(name = "Question API", description = "CRUD operations for quiz questions")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    // ── GET All (paginated) ───────────────────────────────────────────────────
    @Operation(summary = "Get all questions with pagination")
    @GetMapping("allQuestion")
    public ResponseEntity<Page<Question>> getAllQuestion(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")             @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")            @RequestParam(defaultValue = "id") String sortBy) {
        return questionService.getAllQuestions(page, size, sortBy);
    }

    // ── GET By ID ─────────────────────────────────────────────────────────────
    @Operation(summary = "Get a question by its ID")
    @GetMapping("{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Integer id) {
        return questionService.getQuestionById(id);
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

    // ── POST Add ──────────────────────────────────────────────────────────────
    @Operation(summary = "Add a new question")
    @PostMapping("add")
    public ResponseEntity<String> addQuestion(@Valid @RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    // ── PUT Update ────────────────────────────────────────────────────────────
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
