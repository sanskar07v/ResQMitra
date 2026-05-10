package com.resqmitra.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.resqmitra.dto.ErrorResponse;
import com.resqmitra.module.auth.exception.UnauthorizedUserException;
import com.resqmitra.module.auth.exception.UserIdAndPasswordNotMatchException;
import com.resqmitra.module.incident.exception.IncidentNotFoundException;
import com.resqmitra.module.user.exception.UserAlreadyCreatedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice   // ✅ Automatically adds @ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    // ------------------- 404 NOT FOUND -------------------
    @ExceptionHandler({ UsernameNotFoundException.class, IncidentNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // ------------------- 409 CONFLICT -------------------
    @ExceptionHandler(UserAlreadyCreatedException.class)
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex, HttpServletRequest request) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // ------------------- 401 UNAUTHORIZED -------------------
    @ExceptionHandler({ UnauthorizedUserException.class, UserIdAndPasswordNotMatchException.class })
    public ResponseEntity<ErrorResponse> handleUnauthorized(RuntimeException ex, HttpServletRequest request) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    // ------------------- 400 BAD REQUEST (Validation) -------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // Take the first validation error as main message for clarity
        String mainMessage = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        ErrorResponse err = new ErrorResponse(LocalDateTime.now(), mainMessage, request.getRequestURI());
        err.setValidationErrors(errors);

        log.warn("Validation error at {}: {}", request.getRequestURI(), errors);
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    // ------------------- 500 INTERNAL SERVER ERROR -------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    // ------------------- Utility -------------------
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, String path) {
        ErrorResponse err = new ErrorResponse(LocalDateTime.now(), message, path);
        return new ResponseEntity<>(err, status);
    }
}
