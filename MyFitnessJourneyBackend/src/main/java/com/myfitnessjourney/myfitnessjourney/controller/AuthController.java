package com.myfitnessjourney.myfitnessjourney.controller;

import com.myfitnessjourney.myfitnessjourney.dto.LoginRequest;
import com.myfitnessjourney.myfitnessjourney.dto.LoginResponse;
import com.myfitnessjourney.myfitnessjourney.entity.User;
import com.myfitnessjourney.myfitnessjourney.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Check if user exists and has a password (not OAuth2 only user)
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(new LoginResponse(null, null, "Invalid email or password"));
            }
            
            if (user.getPassword() == null) {
                return ResponseEntity.status(401)
                        .body(new LoginResponse(null, null, "This account uses social login. Please use Google or Facebook to sign in."));
            }
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401)
                        .body(new LoginResponse(null, null, "Invalid email or password"));
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LoginResponse.UserDto userDto = new LoginResponse.UserDto(user.getId(), user.getEmail());

            LoginResponse response = new LoginResponse(null, userDto, "Login successful");

            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(null, null, "Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(null, null, "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse(null, null, "Email already exists"));
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user = userRepository.save(user);

        LoginResponse.UserDto userDto = new LoginResponse.UserDto(user.getId(), user.getEmail());

        return ResponseEntity.ok(new LoginResponse(null, userDto, "Registration successful"));
    }

    @GetMapping("/oauth2/success")
    public void oauth2Success(OAuth2AuthenticationToken authentication, 
                              jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        try {
            OAuth2User oauth2User = authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            final String provider = authentication.getAuthorizedClientRegistrationId();
            
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
                String frontendUrl = System.getenv("FRONTEND_URL") != null ? 
                        System.getenv("FRONTEND_URL") : "http://localhost:3000";
                response.sendRedirect(frontendUrl + "/login?error=email_not_provided");
                return;
            }

            User user = userRepository.findByEmail(finalEmail)
                    .orElseGet(() -> {
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
                user.setOauth2Provider(provider);
                user.setOauth2ProviderId(finalProviderId);
                if (finalName != null) user.setName(finalName);
                if (finalPicture != null) user.setPictureUrl(finalPicture);
                user = userRepository.save(user);
            }
            
            // Redirect to frontend - session is automatically created by Spring Security
            String frontendUrl = System.getenv("FRONTEND_URL") != null ? 
                    System.getenv("FRONTEND_URL") : "http://localhost:3000";
            response.sendRedirect(frontendUrl + "/oauth2/callback?userId=" + user.getId() + 
                    "&email=" + java.net.URLEncoder.encode(user.getEmail(), "UTF-8"));
        } catch (Exception e) {
            String frontendUrl = System.getenv("FRONTEND_URL") != null ? 
                    System.getenv("FRONTEND_URL") : "http://localhost:3000";
            response.sendRedirect(frontendUrl + "/login?error=oauth2_failed");
        }
    }
}

