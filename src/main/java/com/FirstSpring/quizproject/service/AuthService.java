package com.FirstSpring.quizproject.service;

import com.FirstSpring.quizproject.dao.UserDao;
import com.FirstSpring.quizproject.dto.AuthResponseDto;
import com.FirstSpring.quizproject.dto.LoginRequestDto;
import com.FirstSpring.quizproject.dto.RegisterRequestDto;
import com.FirstSpring.quizproject.model.Role;
import com.FirstSpring.quizproject.model.User;
import com.FirstSpring.quizproject.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@SuppressWarnings("null")
public class AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userDao.existsByUsername(request.getUsername().trim())) {
            throw new IllegalArgumentException("Username '" + request.getUsername().trim() + "' is already registered");
        }

        Role assignedRole = Role.ROLE_STUDENT;
        if (request.getRole() != null) {
            String r = request.getRole().trim().toUpperCase();
            if (r.contains("ADMIN") || r.contains("TEACHER")) {
                assignedRole = Role.ROLE_ADMIN;
            }
        }

        User user = User.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword().trim()))
                .fullName(Optional.ofNullable(request.getFullName()).map(String::trim).orElse(request.getUsername()))
                .role(assignedRole)
                .build();

        User saved = userDao.save(user);

        String token = jwtService.generateToken(saved, saved.getRole().name(), saved.getFullName());

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .username(saved.getUsername())
                .fullName(saved.getFullName())
                .role(saved.getRole().name())
                .expiresInMs(jwtService.getExpirationMs())
                .build();
    }

    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword().trim())
        );

        User user = userDao.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + request.getUsername()));

        String token = jwtService.generateToken(user, user.getRole().name(), user.getFullName());

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .expiresInMs(jwtService.getExpirationMs())
                .build();
    }

    public AuthResponseDto getCurrentUser(String username) {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return AuthResponseDto.builder()
                .token(null)
                .tokenType("Bearer")
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .expiresInMs(jwtService.getExpirationMs())
                .build();
    }
}
