package com.FirstSpring.quizproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credentials for user login")
public class LoginRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Schema(description = "Login username", example = "admin")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "User password", example = "admin123")
    private String password;
}
