package com.myfitnessjourney.exception;

import com.myfitnessjourney.dto.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<LoginResponse> handleInvalidLogin(InvalidLoginException ex) {
        logger.warn("Invalid login: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(null, null, ex.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "User not found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        logger.warn("Email already exists: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                "Email already exists"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(InvalidEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleInvalidEmailException(InvalidEmailException ex) {
        logger.warn("Invalid email: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid email format"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(WeakPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleWeakPasswordException(WeakPasswordException ex) {
        logger.warn("Weak password: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Password does not meet requirements"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        logger.warn("Username already exists: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                "Username already exists"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(InvalidUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleInvalidUsernameException(InvalidUsernameException ex) {
        logger.warn("Invalid username: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid username format"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid argument"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                "Invalid input",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("error", "User not found");
        response.put("status", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MealNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleMealNotFoundException(MealNotFoundException ex) {
        logger.warn("Meal not found: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("error", "Meal not found");
        response.put("status", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DietNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleDietNotFoundException(DietNotFoundException ex) {
        logger.warn("Diet not found: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("error", "Diet not found");
        response.put("status", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(FitnessProgramNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleFitnessProgramNotFoundException(FitnessProgramNotFoundException ex) {
        return buildNotFoundResponse("Fitness program not found", ex);
    }

    @ExceptionHandler(ExerciseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleExerciseNotFoundException(ExerciseNotFoundException ex) {
        return buildNotFoundResponse("Exercise not found", ex);
    }
    
    private ResponseEntity<ApiError> buildNotFoundResponse(String errorMessage, RuntimeException ex) {
        logger.warn("{}: {}", errorMessage, ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                errorMessage,
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception: ", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<LoginResponse> handleOtherExceptions(Exception ex) {
        logger.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new LoginResponse(null, null, "An unexpected error occurred"));
    }
}

