package com.FirstSpring.quizproject;

import com.FirstSpring.quizproject.dao.QuestionDao;
import com.FirstSpring.quizproject.dao.QuizDao;
import com.FirstSpring.quizproject.dto.QuestionWrapper;
import com.FirstSpring.quizproject.dto.QuizResultDto;
import com.FirstSpring.quizproject.dto.Response;
import com.FirstSpring.quizproject.service.QuizService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for QuizService verifying Java 8 features:
 * - Streams & Collectors (toMap, toList, count)
 * - Optional chaining
 * - Functional Strategy lambdas
 */
@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizDao quizDao;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private QuizService quizService;

    private Quiz sampleQuiz;
    private Question q1;
    private Question q2;

    @BeforeEach
    void setUp() {
        q1 = new Question();
        q1.setId(101);
        q1.setQuestionTitle("Which Java 8 feature enables functional programming?");
        q1.setOption1("Lambdas");
        q1.setOption2("Applets");
        q1.setOption3("AWT");
        q1.setOption4("Swing");
        q1.setRightAnswer("Lambdas");
        q1.setCategory("Java");
        q1.setDifficultyLevel("Easy");

        q2 = new Question();
        q2.setId(102);
        q2.setQuestionTitle("Which class avoids NullPointerException in Java 8?");
        q2.setOption1("Optional");
        q2.setOption2("Nullable");
        q2.setOption3("SafeObject");
        q2.setOption4("Wrapper");
        q2.setRightAnswer("Optional");
        q2.setCategory("Java");
        q2.setDifficultyLevel("Medium");

        sampleQuiz = new Quiz();
        sampleQuiz.setId(1);
        sampleQuiz.setTitle("Java 8 Assessment");
        sampleQuiz.setQuestions(Arrays.asList(q1, q2));
    }

    @Test
    @DisplayName("Verify Quiz Creation using Java 8 Streams and Shuffle")
    void testCreateQuiz() {
        when(questionDao.findAll()).thenReturn(Arrays.asList(q1, q2));
        when(quizDao.save(any(Quiz.class))).thenAnswer(invocation -> {
            Quiz q = invocation.getArgument(0);
            q.setId(99);
            return q;
        });

        ResponseEntity<String> response = quizService.createQuiz("Java", 2, "Test Java Quiz");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("99"));
        verify(quizDao, times(1)).save(any(Quiz.class));
    }

    @Test
    @DisplayName("Verify getQuizQuestions transforms to safe QuestionWrapper via Streams")
    void testGetQuizQuestions() {
        when(quizDao.findById(1)).thenReturn(Optional.of(sampleQuiz));

        ResponseEntity<List<QuestionWrapper>> response = quizService.getQuizQuestions(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuestionWrapper> wrappers = response.getBody();
        assertNotNull(wrappers);
        assertEquals(2, wrappers.size());
        assertEquals("Which Java 8 feature enables functional programming?", wrappers.get(0).getQuestionTitle());
    }

    @Test
    @DisplayName("Verify Quiz Submission & Score Calculation via Streams & Standard Strategy")
    void testSubmitQuizStandardScoring() {
        when(quizDao.findById(1)).thenReturn(Optional.of(sampleQuiz));

        // Submit 1 correct ("Lambdas") and 1 wrong ("Nullable")
        List<Response> userResponses = Arrays.asList(
                new Response(101, "Lambdas"),
                new Response(102, "Nullable")
        );

        ResponseEntity<QuizResultDto> response = quizService.submitQuiz(1, userResponses, "STANDARD");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        QuizResultDto result = response.getBody();
        assertNotNull(result);
        assertEquals(2, result.getTotalQuestions());
        assertEquals(1, result.getCorrectAnswers());
        assertEquals(1, result.getIncorrectAnswers());
        assertEquals(1.0, result.getFinalScore());
        assertEquals(50.0, result.getPercentage());
    }

    @Test
    @DisplayName("Verify Quiz Submission with Negative Marking Lambda Strategy")
    void testSubmitQuizNegativeMarkingStrategy() {
        when(quizDao.findById(1)).thenReturn(Optional.of(sampleQuiz));

        // 1 correct, 1 wrong -> 1 - (1 * 0.25) = 0.75
        List<Response> userResponses = Arrays.asList(
                new Response(101, "Lambdas"),
                new Response(102, "Nullable")
        );

        ResponseEntity<QuizResultDto> response = quizService.submitQuiz(1, userResponses, "NEGATIVE");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        QuizResultDto result = response.getBody();
        assertNotNull(result);
        assertEquals(0.75, result.getFinalScore());
    }
}
