package com.FirstSpring.quizproject.service;

import com.FirstSpring.quizproject.Question;
import com.FirstSpring.quizproject.dao.QuestionDao;
import com.FirstSpring.quizproject.dto.QuestionWrapper;
import com.FirstSpring.quizproject.dto.QuizAnalyticsDto;
import com.FirstSpring.quizproject.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service demonstrating core Java 8 features:
 * - Lambdas & Method References (Question::getCategory, QuestionWrapper::new)
 * - Stream API (filter, map, sorted, collect, groupingBy, distinct)
 * - Optional (orElseThrow, ofNullable, map, filter)
 * - Java 8 Collection Enhancements (Collections.shuffle, Map grouping, Comparator.comparing)
 */
@Service
@SuppressWarnings("null")
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    // ── Get All (with pagination) ─────────────────────────────────────────────

    public ResponseEntity<Page<Question>> getAllQuestions(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return new ResponseEntity<>(questionDao.findAll(pageable), HttpStatus.OK);
    }

    // ── Java 8 Streams: Convert All Questions to QuestionWrappers (Safe DTOs) ──

    public ResponseEntity<List<QuestionWrapper>> getAllQuestionWrappers() {
        // Stream pipeline with method reference constructor QuestionWrapper::new
        List<QuestionWrapper> wrappers = questionDao.findAll()
                .stream()
                .map(QuestionWrapper::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    // ── Java 8 Optional: Get By ID ────────────────────────────────────────────

    public ResponseEntity<Question> getQuestionById(Integer id) {
        // Java 8 Optional.orElseThrow with lambda supplier
        Question question = questionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    // ── Java 8 Optional & Streams: Extract Active/Non-null Options ─────────────

    public ResponseEntity<List<String>> getActiveOptions(Integer id) {
        // Java 8 Optional chaining with Stream.of, filtering and collecting
        List<String> options = questionDao.findById(id)
                .map(q -> Stream.of(q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4())
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    // ── Get By Category ───────────────────────────────────────────────────────

    public ResponseEntity<List<Question>> getQuestionByCategory(String category) {
        List<Question> questions = questionDao.findByCategory(category);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    // ── Get By Difficulty ─────────────────────────────────────────────────────

    public ResponseEntity<List<Question>> getQuestionsByDifficulty(String level) {
        List<Question> questions = questionDao.findByDifficultyLevel(level);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    // ── Get By Category + Difficulty ─────────────────────────────────────────

    public ResponseEntity<List<Question>> getQuestionsByCategoryAndDifficulty(String category, String difficulty) {
        List<Question> questions = questionDao.findByCategoryAndDifficultyLevel(category, difficulty);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    // ── Java 8 Streams & Lambdas: Dynamic Composable Multi-Criteria Search ────

    public ResponseEntity<List<Question>> searchQuestions(
            String keyword,
            String category,
            String difficulty,
            String sortBy,
            String sortDirection) {

        // Java 8 Predicate composition using lambdas
        Predicate<Question> filterPredicate = q -> true;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKw = keyword.trim().toLowerCase();
            Predicate<Question> keywordPredicate = q ->
                    (q.getQuestionTitle() != null && q.getQuestionTitle().toLowerCase().contains(lowerKw)) ||
                    (q.getOption1() != null && q.getOption1().toLowerCase().contains(lowerKw)) ||
                    (q.getOption2() != null && q.getOption2().toLowerCase().contains(lowerKw)) ||
                    (q.getOption3() != null && q.getOption3().toLowerCase().contains(lowerKw)) ||
                    (q.getOption4() != null && q.getOption4().toLowerCase().contains(lowerKw));
            filterPredicate = filterPredicate.and(keywordPredicate);
        }

        if (category != null && !category.trim().isEmpty()) {
            filterPredicate = filterPredicate.and(q ->
                    q.getCategory() != null && q.getCategory().equalsIgnoreCase(category.trim()));
        }

        if (difficulty != null && !difficulty.trim().isEmpty()) {
            filterPredicate = filterPredicate.and(q ->
                    q.getDifficultyLevel() != null && q.getDifficultyLevel().equalsIgnoreCase(difficulty.trim()));
        }

        // Java 8 Comparator with method references
        Comparator<Question> comparator;
        if ("title".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Question::getQuestionTitle, String.CASE_INSENSITIVE_ORDER);
        } else if ("category".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Question::getCategory, String.CASE_INSENSITIVE_ORDER);
        } else if ("difficulty".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Question::getDifficultyLevel, String.CASE_INSENSITIVE_ORDER);
        } else {
            comparator = Comparator.comparing(Question::getId);
        }

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        // Java 8 Stream pipeline: filter -> sorted -> collect
        List<Question> results = questionDao.findAll()
                .stream()
                .filter(filterPredicate)
                .sorted(comparator)
                .collect(Collectors.toList());

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    // ── Java 8 Streams: Distinct Sorted Categories ────────────────────────────

    public ResponseEntity<List<String>> getAllCategories() {
        // Stream -> map -> filter -> distinct -> sorted -> collect
        List<String> categories = questionDao.findAll()
                .stream()
                .map(Question::getCategory)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // ── Java 8 Stream Collectors: GroupingBy & Counting (Analytics) ───────────

    public ResponseEntity<QuizAnalyticsDto> getQuizAnalytics() {
        List<Question> allQuestions = questionDao.findAll();

        // 1. Questions count per category via Collectors.groupingBy & Collectors.counting
        Map<String, Long> countByCategory = allQuestions.stream()
                .filter(q -> q.getCategory() != null)
                .collect(Collectors.groupingBy(Question::getCategory, Collectors.counting()));

        // 2. Questions count per difficulty level
        Map<String, Long> countByDifficulty = allQuestions.stream()
                .filter(q -> q.getDifficultyLevel() != null)
                .collect(Collectors.groupingBy(Question::getDifficultyLevel, Collectors.counting()));

        // 3. Two-level grouping: Category -> (Difficulty -> Count)
        Map<String, Map<String, Long>> countByCategoryAndDifficulty = allQuestions.stream()
                .filter(q -> q.getCategory() != null && q.getDifficultyLevel() != null)
                .collect(Collectors.groupingBy(
                        Question::getCategory,
                        Collectors.groupingBy(Question::getDifficultyLevel, Collectors.counting())
                ));

        // 4. Distinct category list
        List<String> categories = allQuestions.stream()
                .map(Question::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        QuizAnalyticsDto dto = QuizAnalyticsDto.builder()
                .totalQuestions(allQuestions.size())
                .categories(categories)
                .countByCategory(countByCategory)
                .countByDifficulty(countByDifficulty)
                .countByCategoryAndDifficulty(countByCategoryAndDifficulty)
                .build();

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // ── Java 8 Streams: Group Questions By Category ───────────────────────────

    public ResponseEntity<Map<String, List<QuestionWrapper>>> getQuestionsGroupedByCategory() {
        // Grouping and mapping elements simultaneously using Collectors.mapping
        Map<String, List<QuestionWrapper>> grouped = questionDao.findAll()
                .stream()
                .filter(q -> q.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Question::getCategory,
                        Collectors.mapping(QuestionWrapper::new, Collectors.toList())
                ));

        return new ResponseEntity<>(grouped, HttpStatus.OK);
    }

    // ── Java 8 Collections & Streams: Random Sample of Questions ──────────────

    public ResponseEntity<List<QuestionWrapper>> getRandomQuestions(String category, int count) {
        // Filter by category using Streams
        List<Question> pool = questionDao.findAll()
                .stream()
                .filter(q -> category == null || category.trim().isEmpty() ||
                        (q.getCategory() != null && q.getCategory().equalsIgnoreCase(category.trim())))
                .collect(Collectors.toCollection(ArrayList::new));

        // Java 8 Collections shuffle
        Collections.shuffle(pool);

        // Limit stream to requested count and map to QuestionWrapper
        List<QuestionWrapper> randomQuestions = pool.stream()
                .limit(Math.max(1, count))
                .map(QuestionWrapper::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(randomQuestions, HttpStatus.OK);
    }

    // ── Add Question ──────────────────────────────────────────────────────────

    public ResponseEntity<String> addQuestion(Question question) {
        questionDao.save(question);
        return new ResponseEntity<>("Question added successfully", HttpStatus.CREATED);
    }

    // ── Java 8 Optional: Update Question ──────────────────────────────────────

    public ResponseEntity<Question> updateQuestion(Integer id, Question updatedQuestion) {
        // Find existing using Optional.orElseThrow
        Question existing = questionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        // Optional-based safe updates for optional fields
        existing.setQuestionTitle(updatedQuestion.getQuestionTitle());
        existing.setOption1(updatedQuestion.getOption1());
        existing.setOption2(updatedQuestion.getOption2());
        existing.setOption3(Optional.ofNullable(updatedQuestion.getOption3()).map(String::trim).orElse(null));
        existing.setOption4(Optional.ofNullable(updatedQuestion.getOption4()).map(String::trim).orElse(null));
        existing.setRightAnswer(updatedQuestion.getRightAnswer());
        existing.setDifficultyLevel(updatedQuestion.getDifficultyLevel());
        existing.setCategory(updatedQuestion.getCategory());

        Question saved = questionDao.save(existing);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    // ── Java 8 Optional: Delete Question ──────────────────────────────────────

    public ResponseEntity<String> deleteQuestion(Integer id) {
        Question question = questionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        questionDao.delete(question);
        return new ResponseEntity<>("Question deleted successfully", HttpStatus.OK);
    }
}
