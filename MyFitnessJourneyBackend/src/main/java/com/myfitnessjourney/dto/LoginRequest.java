package com.myfitnessjourney.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 1, message = "Password cannot be empty")
    private String password;
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
}

