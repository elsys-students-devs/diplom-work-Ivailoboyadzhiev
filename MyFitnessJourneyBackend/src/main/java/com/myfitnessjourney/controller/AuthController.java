package com.myfitnessjourney.controller;

import com.myfitnessjourney.dto.LoginRequest;
import com.myfitnessjourney.dto.LoginResponse;
import com.myfitnessjourney.entity.User;
import com.myfitnessjourney.repository.UserRepository;
import com.myfitnessjourney.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@AllArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, 
                                                HttpServletRequest httpRequest,
                                                HttpServletResponse httpResponse) {
        try {
            // Authenticate user - this validates credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            //authentication in SecurityContext
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            
            securityContextRepository.saveContext(securityContext, httpRequest, httpResponse);
            
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            LoginResponse.UserDto userDto = new LoginResponse.UserDto(
                    user.getId(), 
                    user.getEmail(), 
                    user.getUsername(), 
                    user.getName()
            );
            
            logger.info("Login successful for user: {}, session created", request.getEmail());
            
            return ResponseEntity.ok(new LoginResponse(null, userDto, "Login successful"));
        } catch (Exception e) {
            logger.error("Login failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody LoginRequest request,
                                                 HttpServletRequest httpRequest,
                                                 HttpServletResponse httpResponse) {
        try {
            LoginResponse.UserDto userDto = userService.register(request.getEmail(), request.getPassword(), request.getUsername());
            
            // Authenticate the newly registered user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Set authentication in SecurityContext
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            
            // Save security context to session
            securityContextRepository.saveContext(securityContext, httpRequest, httpResponse);
            
            logger.info("Registration and auto-login successful for user: {}", request.getEmail());
            
            return ResponseEntity.ok(new LoginResponse(null, userDto, "Registration successful"));
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse.UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(new LoginResponse.UserDto(user.getId(), user.getEmail(), user.getUsername(), user.getName()));
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

