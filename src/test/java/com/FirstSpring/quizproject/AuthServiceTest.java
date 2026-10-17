package com.FirstSpring.quizproject;

import com.FirstSpring.quizproject.dao.UserDao;
import com.FirstSpring.quizproject.dto.AuthResponseDto;
import com.FirstSpring.quizproject.dto.LoginRequestDto;
import com.FirstSpring.quizproject.dto.RegisterRequestDto;
import com.FirstSpring.quizproject.model.Role;
import com.FirstSpring.quizproject.model.User;
import com.FirstSpring.quizproject.security.JwtService;
import com.FirstSpring.quizproject.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User sampleAdmin;
    private User sampleStudent;

    @BeforeEach
    void setUp() {
        sampleAdmin = User.builder()
                .id(1)
                .username("admin")
                .password("encoded_pass")
                .fullName("Admin User")
                .role(Role.ROLE_ADMIN)
                .build();

        sampleStudent = User.builder()
                .id(2)
                .username("student")
                .password("encoded_pass")
                .fullName("Student User")
                .role(Role.ROLE_STUDENT)
                .build();
    }

    @Test
    @DisplayName("Verify successful student registration and JWT issuance")
    void testRegisterStudentSuccess() {
        RegisterRequestDto dto = new RegisterRequestDto("newstudent", "pass123", "New Student", "STUDENT");

        when(userDao.existsByUsername("newstudent")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass123");
        when(userDao.save(any(User.class))).thenReturn(sampleStudent);
        when(jwtService.generateToken(any(User.class), anyString(), anyString())).thenReturn("mock.jwt.token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("student", response.getUsername());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Verify registration fails when username already exists")
    void testRegisterDuplicateUsername() {
        RegisterRequestDto dto = new RegisterRequestDto("admin", "pass123", "Admin User", "ADMIN");

        when(userDao.existsByUsername("admin")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(dto));
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Verify successful login and JWT generation")
    void testLoginSuccess() {
        LoginRequestDto dto = new LoginRequestDto("admin", "admin123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("admin", "admin123"));
        when(userDao.findByUsername("admin")).thenReturn(Optional.of(sampleAdmin));
        when(jwtService.generateToken(sampleAdmin, Role.ROLE_ADMIN.name(), "Admin User")).thenReturn("mock.admin.jwt");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponseDto response = authService.login(dto);

        assertNotNull(response);
        assertEquals("mock.admin.jwt", response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals("ROLE_ADMIN", response.getRole());
    }

    @Test
    @DisplayName("Verify login failure with invalid credentials")
    void testLoginInvalidCredentials() {
        LoginRequestDto dto = new LoginRequestDto("admin", "wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
    }
}
