package com.FirstSpring.quizproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result evaluation DTO produced using Java 8 Stream operations and Lambdas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Detailed quiz evaluation summary and score breakdown")
public class QuizResultDto {

    @Schema(description = "Quiz ID", example = "1")
    private Integer quizId;

    @Schema(description = "Quiz Title", example = "Java 8 Mastery Quiz")
    private String quizTitle;

    @Schema(description = "Total number of questions", example = "5")
    private int totalQuestions;

    @Schema(description = "Number of correct answers", example = "4")
    private int correctAnswers;

    @Schema(description = "Number of incorrect answers", example = "1")
    private int incorrectAnswers;

    @Schema(description = "Calculated final score", example = "4.0")
    private double finalScore;

    @Schema(description = "Percentage achieved", example = "80.0")
    private double percentage;

    @Schema(description = "Performance remark / grade", example = "Great Job!")
    private String remark;

    @Schema(description = "Breakdown per question")
    private List<QuestionFeedbackDto> questionBreakdown;
}
