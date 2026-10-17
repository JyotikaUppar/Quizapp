package com.FirstSpring.quizproject.model;

/**
 * User roles defining access privileges across the Quiz application.
 * - ROLE_ADMIN: Full CRUD privileges on questions, quizzes, and analytics.
 * - ROLE_STUDENT: Read-only access to browse questions and take quizzes.
 */
public enum Role {
    ROLE_ADMIN,
    ROLE_STUDENT
}
