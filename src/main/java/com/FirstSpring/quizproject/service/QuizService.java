package com.FirstSpring.quizproject.service;

import com.FirstSpring.quizproject.Question;
import com.FirstSpring.quizproject.Quiz;
import com.FirstSpring.quizproject.dao.QuestionDao;
import com.FirstSpring.quizproject.dao.QuizDao;
import com.FirstSpring.quizproject.dto.QuestionFeedbackDto;
import com.FirstSpring.quizproject.dto.QuestionWrapper;
import com.FirstSpring.quizproject.dto.QuizResultDto;
import com.FirstSpring.quizproject.dto.Response;
import com.FirstSpring.quizproject.exception.ResourceNotFoundException;
import com.FirstSpring.quizproject.functional.QuizScoringStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service orchestrating Quiz management, evaluation, and scoring.
 * Heavily utilizes Java 8 features:
 * - Stream pipelines (filter, map, collect)
 * - Java 8 Collectors (toMap, toList)
 * - Optional chaining (orElseThrow, ofNullable, map)
 * - Lambdas & Functional Interfaces (QuizScoringStrategy, Function.identity())
 * - Collection algorithms (Collections.shuffle)
 */
@Service
@SuppressWarnings("null")
public class QuizService {

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuestionDao questionDao;

    // ── Create Quiz (Java 8 Streams & Collections.shuffle) ────────────────────

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        // Fetch candidates for category
        List<Question> candidateQuestions = questionDao.findAll()
                .stream()
                .filter(q -> category == null || category.trim().isEmpty() ||
                        (q.getCategory() != null && q.getCategory().equalsIgnoreCase(category.trim())))
                .collect(Collectors.toCollection(ArrayList::new));

        if (candidateQuestions.isEmpty()) {
            throw new ResourceNotFoundException("No questions available for category: " + category);
        }

        // Java 8 Collections shuffle for random selection
        Collections.shuffle(candidateQuestions);

        // Java 8 Stream limit & collect
        List<Question> selectedQuestions = candidateQuestions.stream()
                .limit(Math.max(1, numQ))
                .collect(Collectors.toList());

        Quiz quiz = new Quiz();
        quiz.setTitle(Optional.ofNullable(title)
                .filter(t -> !t.trim().isEmpty())
                .orElse(category + " Quiz (" + selectedQuestions.size() + " Qs)"));
        quiz.setQuestions(selectedQuestions);

        Quiz saved = quizDao.save(quiz);
        return new ResponseEntity<>("Quiz created successfully with ID: " + saved.getId(), HttpStatus.CREATED);
    }

    // ── Get Quiz Questions as Safe Wrappers (Java 8 Optional & Streams) ───────

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer quizId) {
        // Java 8 Optional + Stream map with constructor reference
        Quiz quiz = quizDao.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        List<QuestionWrapper> wrappers = quiz.getQuestions()
                .stream()
                .map(QuestionWrapper::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    // ── Submit Quiz & Calculate Score (Java 8 Streams, Lambdas & Optional) ────

    public ResponseEntity<QuizResultDto> submitQuiz(
            Integer quizId,
            List<Response> responses,
            String scoringStrategyType) {

        Quiz quiz = quizDao.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        List<Question> questions = quiz.getQuestions();

        // 1. Build map of user's submitted answers
        Map<Integer, String> userResponseMap = Optional.ofNullable(responses)
                .orElse(Collections.emptyList())
                .stream()
                .filter(r -> r.getId() != null)
                .collect(Collectors.toMap(
                        Response::getId,
                        r -> Optional.ofNullable(r.getResponse()).map(String::trim).orElse(""),
                        (existing, replacement) -> replacement // in case of duplicate IDs
                ));

        // 3. Evaluate each question using Java 8 Stream pipeline & Lambdas
        List<QuestionFeedbackDto> breakdown = questions.stream()
                .map(question -> {
                    String userAnswer = userResponseMap.getOrDefault(question.getId(), "No answer provided");
                    String correctAnswer = Optional.ofNullable(question.getRightAnswer()).map(String::trim).orElse("");
                    boolean isCorrect = !correctAnswer.isEmpty() && correctAnswer.equalsIgnoreCase(userAnswer.trim());

                    return QuestionFeedbackDto.builder()
                            .questionId(question.getId())
                            .questionTitle(question.getQuestionTitle())
                            .yourAnswer(userAnswer)
                            .correctAnswer(correctAnswer)
                            .isCorrect(isCorrect)
                            .build();
                })
                .collect(Collectors.toList());

        // 4. Aggregate counts via Java 8 Streams & Lambdas
        int totalQuestions = questions.size();
        int correctCount = (int) breakdown.stream().filter(QuestionFeedbackDto::isCorrect).count();
        int incorrectCount = totalQuestions - correctCount;

        // 5. Select Java 8 Functional Strategy using Lambdas
        QuizScoringStrategy strategy;
        String strategyKey = Optional.ofNullable(scoringStrategyType)
                .map(String::trim)
                .map(String::toUpperCase)
                .orElse("STANDARD");

        switch (strategyKey) {
            case "NEGATIVE":
                strategy = QuizScoringStrategy.NEGATIVE_MARKING;
                break;
            case "PERCENTAGE":
                strategy = QuizScoringStrategy.PERCENTAGE;
                break;
            case "STANDARD":
            default:
                strategy = QuizScoringStrategy.STANDARD;
                break;
        }

        double finalScore = strategy.calculate(correctCount, totalQuestions, incorrectCount);
        double percentage = totalQuestions == 0 ? 0.0 : ((double) correctCount / totalQuestions) * 100.0;

        // 6. Generate remark using functional thresholds
        String remark;
        if (percentage >= 90) remark = "Outstanding Performance! 🌟";
        else if (percentage >= 75) remark = "Great Job! Well done! 👍";
        else if (percentage >= 50) remark = "Good effort! Practice more to improve. 💡";
        else remark = "Keep practicing! You can do better next time. 📚";

        QuizResultDto resultDto = QuizResultDto.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .totalQuestions(totalQuestions)
                .correctAnswers(correctCount)
                .incorrectAnswers(incorrectCount)
                .finalScore(Math.round(finalScore * 100.0) / 100.0)
                .percentage(Math.round(percentage * 10.0) / 10.0)
                .remark(remark)
                .questionBreakdown(breakdown)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // ── Get All Quizzes ───────────────────────────────────────────────────────

    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return new ResponseEntity<>(quizDao.findAll(), HttpStatus.OK);
    }

    // ── Delete Quiz (Java 8 Optional) ─────────────────────────────────────────

    public ResponseEntity<String> deleteQuiz(Integer id) {
        Quiz quiz = quizDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        quizDao.delete(quiz);
        return new ResponseEntity<>("Quiz deleted successfully", HttpStatus.OK);
    }
}
