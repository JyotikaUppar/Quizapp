package com.FirstSpring.quizproject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Schema(description = "Represents a quiz question with multiple choice options")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the question", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @NotBlank(message = "Question title must not be blank")
    @Size(min = 5, max = 500, message = "Question title must be between 5 and 500 characters")
    @Schema(description = "The question text", example = "What is the capital of France?")
    private String questionTitle;

    @NotBlank(message = "Option 1 must not be blank")
    @Schema(description = "First answer option", example = "Paris")
    private String option1;

    @NotBlank(message = "Option 2 must not be blank")
    @Schema(description = "Second answer option", example = "London")
    private String option2;

    @Schema(description = "Third answer option (optional)", example = "Berlin")
    private String option3;

    @Schema(description = "Fourth answer option (optional)", example = "Rome")
    private String option4;

    @NotBlank(message = "Right answer must not be blank")
    @Schema(description = "The correct answer", example = "Paris")
    private String rightAnswer;

    @NotBlank(message = "Difficulty level must not be blank")
    @Schema(description = "Difficulty level: Easy, Medium, Hard", example = "Easy")
    private String difficultyLevel;

    @NotBlank(message = "Category must not be blank")
    @Schema(description = "Category of the question", example = "Geography")
    private String category;
}
