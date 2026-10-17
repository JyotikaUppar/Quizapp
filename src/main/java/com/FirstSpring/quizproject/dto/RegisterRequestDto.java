package com.FirstSpring.quizproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for registering a new user account")
public class RegisterRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Unique username", example = "candidate1")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 4, max = 100, message = "Password must be at least 4 characters")
    @Schema(description = "Account password", example = "secret123")
    private String password;

    @Schema(description = "Full name of the user", example = "Alex Candidate")
    private String fullName;

    @Schema(description = "Desired role: ADMIN or STUDENT (defaults to STUDENT)", example = "STUDENT")
    private String role;
}
