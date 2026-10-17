package com.FirstSpring.quizproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response containing JWT bearer token and user role")
public class AuthResponseDto {

    @Schema(description = "JWT Bearer access token")
    private String token;

    @Schema(description = "Token type (Bearer)", example = "Bearer")
    private String tokenType;

    @Schema(description = "Username of the authenticated user", example = "admin")
    private String username;

    @Schema(description = "Full name of the user", example = "System Administrator")
    private String fullName;

    @Schema(description = "Role assigned to the user: ROLE_ADMIN or ROLE_STUDENT", example = "ROLE_ADMIN")
    private String role;

    @Schema(description = "Token expiration duration in milliseconds", example = "86400000")
    private long expiresInMs;
}
