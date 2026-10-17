package com.FirstSpring.quizproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Analytics DTO computed with Java 8 Streams, Collectors.groupingBy, and counting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Aggregated quiz bank statistics calculated using Java 8 Stream API")
public class QuizAnalyticsDto {

    @Schema(description = "Total number of questions in database", example = "50")
    private long totalQuestions;

    @Schema(description = "Distinct category names available")
    private List<String> categories;

    @Schema(description = "Questions count grouped by category")
    private Map<String, Long> countByCategory;

    @Schema(description = "Questions count grouped by difficulty level")
    private Map<String, Long> countByDifficulty;

    @Schema(description = "Two-level grouping: category -> (difficulty -> count)")
    private Map<String, Map<String, Long>> countByCategoryAndDifficulty;
}
