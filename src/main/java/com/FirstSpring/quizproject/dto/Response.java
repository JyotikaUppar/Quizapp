package com.FirstSpring.quizproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a participant's submitted response to a question.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a single question response submitted by a user")
public class Response {

    @NotNull(message = "Question ID is required")
    @Schema(description = "Question ID", example = "1")
    private Integer id;

    @Schema(description = "User's selected answer", example = "Paris")
    private String response;
}
