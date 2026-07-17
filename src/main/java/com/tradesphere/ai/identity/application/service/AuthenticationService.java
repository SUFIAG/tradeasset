package com.tradesphere.ai.identity.application.service;

import com.tradesphere.ai.identity.application.port.in.*;
import com.tradesphere.ai.identity.application.port.out.*;
import com.tradesphere.ai.identity.domain.model.User;
import com.tradesphere.ai.identity.domain.model.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Authentication Service
 * Handles user registration, login, and authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationService implements RegisterUserUseCase, LoginUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtTokenPort jwtTokenPort;
    private final LoadUserRolesPort loadUserRolesPort;

    @Override
    public User register(RegisterUserCommand command) {
        log.info("Registering new user: {}", command.getUsername());

        // Check if username already exists
        if (loadUserPort.loadUserByUsername(command.getUsername()).isPresent()) {
            throw new UserException("Username already exists: " + command.getUsername());
        }

        // Check if email already exists
        if (loadUserPort.loadUserByEmail(command.getEmail()).isPresent()) {
            throw UserException.userAlreadyExists(command.getEmail());
        }

        // Hash password
        String passwordHash = passwordEncoder.encode(command.getPassword());

        // Create user
        User user = User.create(
                command.getTenantId(),
                command.getUsername(),
                command.getEmail(),
                passwordHash,
                command.getFirstName(),
                command.getLastName(),
                command.getCreatedBy()
        );

        // Save user
        User savedUser = saveUserPort.saveUser(user);

        log.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    @Override
    public AuthenticationResult login(LoginCommand command) {
        log.info("User login attempt: {}", command.getUsername());

        // Load user
        User user = loadUserPort.loadUserByUsername(command.getUsername())
                .orElseThrow(UserException::invalidCredentials);

        // Check if account is locked
        if (user.isLocked()) {
            throw UserException.accountLocked();
        }

        // Verify password
        if (!passwordEncoder.matches(command.getPassword(), user.getPasswordHash())) {
            user.recordFailedLogin();
            saveUserPort.saveUser(user);
            throw UserException.invalidCredentials();
        }

        // Record successful login
        user.recordSuccessfulLogin();
        saveUserPort.saveUser(user);

        // Load user roles and permissions
        Set<String> roles = loadUserRolesPort.loadRoleCodesByRoleIds(user.getRoleIds());
        Set<String> permissions = loadUserRolesPort.loadPermissionCodesByRoleIds(user.getRoleIds());

        // Generate tokens
        String accessToken = jwtTokenPort.generateAccessToken(
                user.getId().getValue(),
                user.getUsername(),
                roles,
                permissions
        );

        String refreshToken = jwtTokenPort.generateRefreshToken(user.getId().getValue());

        log.info("User logged in successfully: {}", user.getUsername());

        return AuthenticationResult.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400000) // 24 hours
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}

