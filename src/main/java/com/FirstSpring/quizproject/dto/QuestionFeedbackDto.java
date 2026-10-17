package com.FirstSpring.quizproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detailed per-question feedback generated during quiz evaluation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Individual question evaluation feedback")
public class QuestionFeedbackDto {

    @Schema(description = "Question ID", example = "1")
    private Integer questionId;

    @Schema(description = "Question Title", example = "What is the capital of France?")
    private String questionTitle;

    @Schema(description = "The answer selected by the user", example = "London")
    private String yourAnswer;

    @Schema(description = "The correct answer", example = "Paris")
    private String correctAnswer;

    @Schema(description = "Whether the user's answer was correct", example = "false")
    private boolean isCorrect;
}
