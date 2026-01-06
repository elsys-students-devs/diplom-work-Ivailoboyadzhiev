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

@Service
@AllArgsConstructor
public class UserService {

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
        return new LoginResponse.UserDto(user.getId(), user.getEmail(), user.getUsername(), user.getName());
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
        return new LoginResponse.UserDto(user.getId(), user.getEmail(), user.getUsername(), user.getName());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new com.myfitnessjourney.exception.UserNotFoundException("User not found with email: " + email);
                });
    }
}

