package com.myfitnessjourney.myfitnessjourney.controller;

import com.myfitnessjourney.myfitnessjourney.dto.LoginRequest;
import com.myfitnessjourney.myfitnessjourney.dto.LoginResponse;
import com.myfitnessjourney.myfitnessjourney.entity.User;
import com.myfitnessjourney.myfitnessjourney.repository.UserRepository;
import com.myfitnessjourney.myfitnessjourney.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@AllArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse.UserDto userDto = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(null, userDto, "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody LoginRequest request) {
        LoginResponse.UserDto userDto = userService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(null, userDto, "Registration successful"));
    }

    @GetMapping("/oauth2/success")
    public void oauth2Success(OAuth2AuthenticationToken authentication, 
                              jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        try {
            logger.info("OAuth2 success callback received");
            OAuth2User oauth2User = authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            final String provider = authentication.getAuthorizedClientRegistrationId();
            logger.info("OAuth2 provider: {}", provider);
            
            // Extract email, name, picture, and providerId
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");
            String providerId = (String) attributes.get("sub");

            // Handle Facebook attributes
            if (provider != null && provider.equals("facebook")) {
                if (email == null) {
                    email = (String) attributes.get("email");
                }
                if (name == null) {
                    name = (String) attributes.get("name");
                }
                if (picture == null && attributes.get("picture") != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
                    if (pictureObj != null && pictureObj.get("data") != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) pictureObj.get("data");
                        picture = (String) data.get("url");
                    }
                }
                providerId = (String) attributes.get("id");
            }

            final String finalEmail = email;
            final String finalName = name;
            final String finalPicture = picture;
            final String finalProviderId = providerId;

            if (finalEmail == null) {
                logger.warn("OAuth2 failed: Email not provided by provider: {}", provider);
                String frontendUrl = System.getenv("FRONTEND_URL") != null ? 
                        System.getenv("FRONTEND_URL") : "http://localhost:3000";
                response.sendRedirect(frontendUrl + "/login?error=email_not_provided");
                return;
            }

            User user = userRepository.findByEmail(finalEmail)
                    .orElseGet(() -> {
                        logger.info("Creating new OAuth2 user: {}", finalEmail);
                        User newUser = new User();
                        newUser.setEmail(finalEmail);
                        newUser.setName(finalName);
                        newUser.setPictureUrl(finalPicture);
                        newUser.setOauth2Provider(provider);
                        newUser.setOauth2ProviderId(finalProviderId);
                        return userRepository.save(newUser);
                    });

            // Update OAuth2 info if user exists but doesn't have it
            if (user.getOauth2Provider() == null) {
                logger.info("Updating OAuth2 info for existing user: {}", finalEmail);
                user.setOauth2Provider(provider);
                user.setOauth2ProviderId(finalProviderId);
                if (finalName != null) user.setName(finalName);
                if (finalPicture != null) user.setPictureUrl(finalPicture);
                user = userRepository.save(user);
            }
            
            logger.info("OAuth2 login successful for user: {}", user.getEmail());
            // Redirect to frontend - session is automatically created by Spring Security
            String frontendUrl = System.getenv("FRONTEND_URL") != null ? 
                    System.getenv("FRONTEND_URL") : "http://localhost:3000";
            response.sendRedirect(frontendUrl + "/oauth2/callback?userId=" + user.getId() + 
                    "&email=" + java.net.URLEncoder.encode(user.getEmail(), "UTF-8"));
        } catch (Exception e) {
            logger.error("OAuth2 error: ", e);
            String frontendUrl = System.getenv("FRONTEND_URL") != null ? 
                    System.getenv("FRONTEND_URL") : "http://localhost:3000";
            response.sendRedirect(frontendUrl + "/login?error=oauth2_failed");
        }
    }
}

