package com.FirstSpring.quizproject.dao;

import com.FirstSpring.quizproject.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends JpaRepository<Question, Integer> {

    // Filter by category (case-sensitive)
    List<Question> findByCategory(String category);

    // Filter by difficulty level
    List<Question> findByDifficultyLevel(String difficultyLevel);

    // Filter by both category and difficulty
    List<Question> findByCategoryAndDifficultyLevel(String category, String difficultyLevel);
}
