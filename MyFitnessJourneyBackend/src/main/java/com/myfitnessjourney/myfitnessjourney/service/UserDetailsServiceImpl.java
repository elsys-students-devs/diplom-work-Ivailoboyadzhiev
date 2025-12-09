package com.myfitnessjourney.myfitnessjourney.service;

import com.myfitnessjourney.myfitnessjourney.entity.User;
import com.myfitnessjourney.myfitnessjourney.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        String password = user.getPassword() != null ? user.getPassword() : "{noop}NO_PASSWORD";
        logger.debug("User loaded successfully: {}", email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                password,
                new ArrayList<>()
        );
    }
}

