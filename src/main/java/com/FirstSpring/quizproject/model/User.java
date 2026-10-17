package com.FirstSpring.quizproject.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * User Entity representing system participants (Admins/Teachers and Candidates/Students).
 * Implements Spring Security UserDetails for authentication and RBAC authorization.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User account entity with Role-Based Access Control")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique user ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true)
    @Schema(description = "Unique login username", example = "admin")
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    @Schema(description = "BCrypt-encoded secure password", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "Full name of the user", example = "Jane Doe")
    private String fullName;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Role for authorization: ROLE_ADMIN or ROLE_STUDENT", example = "ROLE_ADMIN")
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
