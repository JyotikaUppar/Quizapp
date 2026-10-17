package com.FirstSpring.quizproject.dto;

import com.FirstSpring.quizproject.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * Data Transfer Object for presenting a question to a quiz participant.
 * Note: Intentionally hides the 'rightAnswer' field for secure quiz delivery.
 * Uses Java 8 Optional for safe field extraction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("null")
@Schema(description = "Question presentation DTO without the correct answer (for quiz takers)")
public class QuestionWrapper {

    @Schema(description = "Question ID", example = "1")
    private Integer id;

    @Schema(description = "Question text", example = "What is the capital of France?")
    private String questionTitle;

    @Schema(description = "Option 1", example = "Paris")
    private String option1;

    @Schema(description = "Option 2", example = "London")
    private String option2;

    @Schema(description = "Option 3 (optional)", example = "Berlin")
    private String option3;

    @Schema(description = "Option 4 (optional)", example = "Rome")
    private String option4;

    @Schema(description = "Difficulty level", example = "Easy")
    private String difficultyLevel;

    @Schema(description = "Category", example = "Geography")
    private String category;

    /**
     * Java 8 Constructor transforming Question entity to QuestionWrapper DTO
     * using Optional to safely sanitize optional fields.
     */
    public QuestionWrapper(Question question) {
        this.id = question.getId();
        this.questionTitle = question.getQuestionTitle();
        this.option1 = question.getOption1();
        this.option2 = question.getOption2();
        // Java 8 Optional handling for nullable/blank options
        this.option3 = Optional.ofNullable(question.getOption3())
                               .map(String::trim)
                               .filter(s -> !s.isEmpty())
                               .orElse(null);
        this.option4 = Optional.ofNullable(question.getOption4())
                               .map(String::trim)
                               .filter(s -> !s.isEmpty())
                               .orElse(null);
        this.difficultyLevel = question.getDifficultyLevel();
        this.category = question.getCategory();
    }
}
