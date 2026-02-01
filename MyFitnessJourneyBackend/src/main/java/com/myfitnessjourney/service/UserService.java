package com.myfitnessjourney.service;

import com.myfitnessjourney.dto.LoginResponse;
import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.exception.EmailAlreadyExistsException;
import com.myfitnessjourney.exception.InvalidLoginException;
import com.myfitnessjourney.exception.UsernameAlreadyExistsException;
import com.myfitnessjourney.repository.UserRepository;
import com.myfitnessjourney.util.ValidationUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class UserService {

    private static final int STREAK_RESET_HOURS = 24;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public LoginResponse.UserDto login(String email, String password) {
        logger.info("Login attempt for email: {}", email);

        // Check if user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed: User not found with email: {}", email);
                    return new InvalidLoginException("Invalid email or password");
                });

        // Check if user has password (not OAuth2 only user)
        if (user.getPassword() == null) {
            logger.warn("Login failed: User {} uses social login only", email);
            throw new InvalidLoginException("This account uses social login. Please use Google or Facebook to sign in.");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed: Invalid password for email: {}", email);
            throw new InvalidLoginException("Invalid email or password");
        }

        // Authenticate with Spring Security
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for email: {}", email);
            throw new InvalidLoginException("Invalid email or password");
        }

        // Reload user to ensure we have the latest data
        user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found after successful authentication: {}", email);
                    return new InvalidLoginException("User not found");
                });

        logger.info("Login successful for user: {}", user.getEmail());
        user = updateLoginStreak(user);
        return toUserDto(user);
    }

    @Transactional
    public User updateLoginStreak(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastLogin = user.getLastLoginAt();
        Integer currentStreak = user.getLoginStreak() != null ? user.getLoginStreak() : 0;

        int newStreak;
        if (lastLogin == null) {
            newStreak = 1;
        } else {
            java.time.LocalDate lastDate = lastLogin.toLocalDate();
            java.time.LocalDate today = now.toLocalDate();
            if (lastDate.equals(today)) {
                newStreak = currentStreak;
            } else if (lastDate.equals(today.minusDays(1))) {
                newStreak = currentStreak + 1;
            } else {
                newStreak = 1;
            }
        }

        user.setLastLoginAt(now);
        user.setLoginStreak(newStreak);
        return userRepository.save(user);
    }

    public int getEffectiveStreak(User user) {
        if (user.getLastLoginAt() == null) {
            return 0;
        }
        long hoursSinceLogin = ChronoUnit.HOURS.between(user.getLastLoginAt(), LocalDateTime.now());
        if (hoursSinceLogin >= STREAK_RESET_HOURS) {
            return 0;
        }
        return user.getLoginStreak() != null ? user.getLoginStreak() : 0;
    }

    public LoginResponse.UserDto getUserDto(User user) {
        LoginResponse.UserDto dto = new LoginResponse.UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setStreak(getEffectiveStreak(user));
        return dto;
    }

    private LoginResponse.UserDto toUserDto(User user) {
        return getUserDto(user);
    }

    public LoginResponse.UserDto register(String email, String password, String username) {
        logger.info("Registration attempt for email: {}, username: {}", email, username);

        // Validate email format
        ValidationUtil.validateEmail(email);

        // Validate password strength
        ValidationUtil.validatePassword(password);

        // Validate username
        ValidationUtil.validateUsername(username);

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            logger.warn("Registration failed: Email already exists: {}", email);
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            logger.warn("Registration failed: Username already exists: {}", username);
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);

        logger.info("Registration successful for user: {} with username: {}", user.getEmail(), user.getUsername());
        user = updateLoginStreak(user);
        return toUserDto(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new com.myfitnessjourney.exception.UserNotFoundException("User not found with email: " + email);
                });
    }
}

