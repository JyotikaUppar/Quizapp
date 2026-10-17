package com.FirstSpring.quizproject;

import com.FirstSpring.quizproject.functional.QuizScoringStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for Java 8 Functional Interface & Lambda expressions.
 */
class QuizScoringStrategyTest {

    @Test
    @DisplayName("Verify Java 8 Standard Scoring Lambda")
    void testStandardScoringLambda() {
        QuizScoringStrategy strategy = QuizScoringStrategy.STANDARD;
        double score = strategy.calculate(8, 10, 2);
        assertEquals(8.0, score);
        assertEquals("8.00", strategy.formatScore(8, 10, 2));
    }

    @Test
    @DisplayName("Verify Java 8 Negative Marking Lambda")
    void testNegativeMarkingLambda() {
        QuizScoringStrategy strategy = QuizScoringStrategy.NEGATIVE_MARKING;
        // 8 correct, 2 incorrect -> 8 - (2 * 0.25) = 7.5
        double score = strategy.calculate(8, 10, 2);
        assertEquals(7.5, score);
        assertEquals("7.50", strategy.formatScore(8, 10, 2));

        // When penalty exceeds correct: Math.max(0.0, ...)
        double floorScore = strategy.calculate(0, 4, 4);
        assertEquals(0.0, floorScore);
    }

    @Test
    @DisplayName("Verify Java 8 Percentage Scoring Lambda")
    void testPercentageScoringLambda() {
        QuizScoringStrategy strategy = QuizScoringStrategy.PERCENTAGE;
        // 7 out of 10 -> 70.0%
        double score = strategy.calculate(7, 10, 3);
        assertEquals(70.0, score, 0.001);
    }

    @Test
    @DisplayName("Verify Custom Lambda Implementation of Functional Interface")
    void testCustomLambdaScoring() {
        // Custom Lambda: 2 points per correct, no penalty
        QuizScoringStrategy doublePoints = (correct, total, incorrect) -> (double) correct * 2.0;
        assertEquals(10.0, doublePoints.calculate(5, 5, 0));
        assertEquals("10.00", doublePoints.formatScore(5, 5, 0));
    }
}
