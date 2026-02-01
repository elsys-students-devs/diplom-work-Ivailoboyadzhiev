package com.myfitnessjourney.config;

import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiUserInitializer implements CommandLineRunner {

    private static final String AI_USER_EMAIL = "groc@myfitnessjourney.ai";
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (!userRepository.findByEmail(AI_USER_EMAIL).isPresent()) {
            User aiUser = new User();
            aiUser.setEmail(AI_USER_EMAIL);
            aiUser.setUsername("groc");
            aiUser.setName("Groc - AI Fitness Assistant");
            aiUser.setPassword(null);
            userRepository.save(aiUser);
            log.info("AI user 'groc' initialized successfully");
        } else {
            log.debug("AI user 'groc' already exists");
        }
    }
}
