package com.FirstSpring.quizproject;

import com.FirstSpring.quizproject.dao.QuestionDao;
import com.FirstSpring.quizproject.dto.QuestionWrapper;
import com.FirstSpring.quizproject.dto.QuizAnalyticsDto;
import com.FirstSpring.quizproject.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests verifying Java 8 features in QuestionService:
 * - Streams (map, filter, distinct, sorted, groupingBy, mapping)
 * - Optional (orElseThrow, ofNullable)
 * - Lambdas (Predicate composition, Comparator)
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private QuestionService questionService;

    private List<Question> sampleQuestions;

    @BeforeEach
    void setUp() {
        Question q1 = new Question();
        q1.setId(1);
        q1.setQuestionTitle("What is a Lambda in Java 8?");
        q1.setOption1("Anonymous function");
        q1.setOption2("Class");
        q1.setOption3("Interface");
        q1.setOption4("Variable");
        q1.setRightAnswer("Anonymous function");
        q1.setCategory("Java");
        q1.setDifficultyLevel("Easy");

        Question q2 = new Question();
        q2.setId(2);
        q2.setQuestionTitle("Which interface is a FunctionalInterface?");
        q2.setOption1("Runnable");
        q2.setOption2("Cloneable");
        q2.setOption3(null); // nullable option
        q2.setOption4("   "); // blank option
        q2.setRightAnswer("Runnable");
        q2.setCategory("Java");
        q2.setDifficultyLevel("Medium");

        Question q3 = new Question();
        q3.setId(3);
        q3.setQuestionTitle("What is Python's list comprehension?");
        q3.setOption1("Concise way to create lists");
        q3.setOption2("A loop");
        q3.setOption3("A function");
        q3.setOption4("A module");
        q3.setRightAnswer("Concise way to create lists");
        q3.setCategory("Python");
        q3.setDifficultyLevel("Easy");

        sampleQuestions = Arrays.asList(q1, q2, q3);
    }

    @Test
    @DisplayName("Verify Java 8 Stream Mapping to QuestionWrapper (hides rightAnswer)")
    void testGetAllQuestionWrappers() {
        when(questionDao.findAll()).thenReturn(sampleQuestions);

        ResponseEntity<List<QuestionWrapper>> response = questionService.getAllQuestionWrappers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());

        // Verify that QuestionWrapper constructor cleanly removed null/blank options
        QuestionWrapper w2 = response.getBody().get(1);
        assertEquals(2, w2.getId());
        assertNull(w2.getOption3());
        assertNull(w2.getOption4());
    }

    @Test
    @DisplayName("Verify Java 8 Optional & Streams for active options extraction")
    void testGetActiveOptions() {
        Question q2 = sampleQuestions.get(1);
        when(questionDao.findById(2)).thenReturn(Optional.of(q2));

        ResponseEntity<List<String>> response = questionService.getActiveOptions(2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Only Option1 ("Runnable") and Option2 ("Cloneable") are non-null and non-blank
        assertEquals(2, response.getBody().size());
        assertEquals(Arrays.asList("Runnable", "Cloneable"), response.getBody());
    }

    @Test
    @DisplayName("Verify Java 8 Streams distinct and sorted categories")
    void testGetAllCategories() {
        when(questionDao.findAll()).thenReturn(sampleQuestions);

        ResponseEntity<List<String>> response = questionService.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> categories = response.getBody();
        assertNotNull(categories);
        assertEquals(2, categories.size());
        // Sorted alphabetically: "Java", "Python"
        assertEquals("Java", categories.get(0));
        assertEquals("Python", categories.get(1));
    }

    @Test
    @DisplayName("Verify Java 8 Collectors.groupingBy and counting for Analytics")
    void testGetQuizAnalytics() {
        when(questionDao.findAll()).thenReturn(sampleQuestions);

        ResponseEntity<QuizAnalyticsDto> response = questionService.getQuizAnalytics();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        QuizAnalyticsDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(3, dto.getTotalQuestions());

        // Category counts: Java -> 2, Python -> 1
        assertEquals(2L, dto.getCountByCategory().get("Java"));
        assertEquals(1L, dto.getCountByCategory().get("Python"));

        // Difficulty counts: Easy -> 2, Medium -> 1
        assertEquals(2L, dto.getCountByDifficulty().get("Easy"));
        assertEquals(1L, dto.getCountByDifficulty().get("Medium"));
    }

    @Test
    @DisplayName("Verify Java 8 Predicate Lambdas in multi-criteria search")
    void testSearchQuestionsWithPredicates() {
        when(questionDao.findAll()).thenReturn(sampleQuestions);

        // Search by keyword "interface" and category "Java"
        ResponseEntity<List<Question>> response = questionService.searchQuestions(
                "interface", "Java", null, "id", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Question> results = response.getBody();
        assertNotNull(results);
        // Matches q1 (has "Interface" in option3) and q2 (has "interface" in questionTitle)
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Verify Java 8 Collectors.mapping in Grouped Questions")
    void testGetQuestionsGroupedByCategory() {
        when(questionDao.findAll()).thenReturn(sampleQuestions);

        ResponseEntity<Map<String, List<QuestionWrapper>>> response =
                questionService.getQuestionsGroupedByCategory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, List<QuestionWrapper>> map = response.getBody();
        assertNotNull(map);
        assertTrue(map.containsKey("Java"));
        assertTrue(map.containsKey("Python"));
        assertEquals(2, map.get("Java").size());
        assertEquals(1, map.get("Python").size());
    }
}
