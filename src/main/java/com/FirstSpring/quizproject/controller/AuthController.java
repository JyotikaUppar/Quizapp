package com.FirstSpring.quizproject.controller;

import com.FirstSpring.quizproject.dto.AuthResponseDto;
import com.FirstSpring.quizproject.dto.LoginRequestDto;
import com.FirstSpring.quizproject.dto.RegisterRequestDto;
import com.FirstSpring.quizproject.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication API", description = "Endpoints for user registration, login, and JWT token issuance")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Register a new user account with role (ADMIN or STUDENT)")
    @PostMapping("register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        AuthResponseDto response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Log in with username and password to receive a JWT access token")
    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get currently authenticated user profile")
    @GetMapping("me")
    public ResponseEntity<AuthResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AuthResponseDto response = authService.getCurrentUser(userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
