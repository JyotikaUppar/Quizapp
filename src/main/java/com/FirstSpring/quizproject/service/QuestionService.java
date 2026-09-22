package com.FirstSpring.quizproject.service;

import com.FirstSpring.quizproject.Question;
import com.FirstSpring.quizproject.dao.QuestionDao;
import com.FirstSpring.quizproject.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    // ── Get All (with pagination) ─────────────────────────────────────────────

    public ResponseEntity<Page<Question>> getAllQuestions(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return new ResponseEntity<>(questionDao.findAll(pageable), HttpStatus.OK);
    }

    // ── Get By ID ─────────────────────────────────────────────────────────────

    public ResponseEntity<Question> getQuestionById(Integer id) {
        Question question = questionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        return new ResponseEntity<>(question, HttpStatus.OK);
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

    // ── Add Question ──────────────────────────────────────────────────────────

    public ResponseEntity<String> addQuestion(Question question) {
        questionDao.save(question);
        return new ResponseEntity<>("Question added successfully", HttpStatus.CREATED);
    }

    // ── Update Question ───────────────────────────────────────────────────────

    public ResponseEntity<Question> updateQuestion(Integer id, Question updatedQuestion) {
        Question existing = questionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        existing.setQuestionTitle(updatedQuestion.getQuestionTitle());
        existing.setOption1(updatedQuestion.getOption1());
        existing.setOption2(updatedQuestion.getOption2());
        existing.setOption3(updatedQuestion.getOption3());
        existing.setOption4(updatedQuestion.getOption4());
        existing.setRightAnswer(updatedQuestion.getRightAnswer());
        existing.setDifficultyLevel(updatedQuestion.getDifficultyLevel());
        existing.setCategory(updatedQuestion.getCategory());

        Question saved = questionDao.save(existing);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    // ── Delete Question ───────────────────────────────────────────────────────

    public ResponseEntity<String> deleteQuestion(Integer id) {
        if (!questionDao.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }
        questionDao.deleteById(id);
        return new ResponseEntity<>("Question deleted successfully", HttpStatus.OK);
    }
}
