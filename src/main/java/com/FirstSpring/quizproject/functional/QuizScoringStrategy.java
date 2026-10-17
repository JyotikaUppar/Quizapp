package com.FirstSpring.quizproject.functional;

/**
 * Java 8 Functional Interface demonstrating custom functional strategy pattern.
 * Allows pluggable scoring algorithms implemented via Lambda expressions.
 */
@FunctionalInterface
public interface QuizScoringStrategy {

    /**
     * Calculates the score based on quiz metrics.
     *
     * @param correctCount   Number of correctly answered questions
     * @param totalCount     Total questions in the quiz
     * @param incorrectCount Number of incorrectly answered questions
     * @return Calculated numerical score
     */
    double calculate(int correctCount, int totalCount, int incorrectCount);

    // ── Predefined Lambda Strategies (Java 8 Static Methods & Lambdas) ──────────

    /** Standard scoring: 1 point per correct answer */
    QuizScoringStrategy STANDARD = (correct, total, incorrect) -> (double) correct;

    /** Negative marking: 1 point per correct answer, -0.25 penalty per wrong answer */
    QuizScoringStrategy NEGATIVE_MARKING = (correct, total, incorrect) ->
            Math.max(0.0, (double) correct - (incorrect * 0.25));

    /** Percentage score: (correct / total) * 100 */
    QuizScoringStrategy PERCENTAGE = (correct, total, incorrect) ->
            total == 0 ? 0.0 : ((double) correct / total) * 100.0;

    // ── Default Method (Java 8 Feature) ───────────────────────────────────────

    /**
     * Java 8 Default method that formats the calculated score.
     */
    default String formatScore(int correct, int total, int incorrect) {
        double score = calculate(correct, total, incorrect);
        return String.format("%.2f", score);
    }
}
