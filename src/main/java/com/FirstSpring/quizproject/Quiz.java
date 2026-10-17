package com.FirstSpring.quizproject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Quiz Entity representing a collection of questions grouped together.
 */
@Entity
@Data
@NoArgsConstructor
@Schema(description = "Represents a Quiz containing a curated list of questions")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the quiz", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(description = "Title or description of the quiz", example = "Core Java 8 Assessment")
    private String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "quiz_questions",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @Schema(description = "List of questions belonging to this quiz")
    private List<Question> questions = new ArrayList<>();
}
