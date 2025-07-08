package vn.lottefinance.landingpage.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import vn.lottefinance.landingpage.exception.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {
private static String err_msg = "500 Internal server";
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableExceptions(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Http message is not readable, please check your message again");
        errors.put("detail", err_msg);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(constraintViolation -> {
            String fieldName = constraintViolation.getPropertyPath().toString();
            String errorMessage = constraintViolation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingServletRequestParameterExceptions(MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), "is missing (" + ex.getParameterType() + ")");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatchExceptions(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            errors.put(ex.getName(), "must be in " + Arrays.toString(ex.getRequiredType().getEnumConstants()));
        } else {
            errors.put("message", "TypeMismatchException");
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CustomedBadRequestException.class)
    public ResponseEntity<Map<String, String>> handleCustomedBadRequestExceptions(CustomedBadRequestException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundExceptions(ResourceNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "ResourceNotFound");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(ResourceAlreadyDisabledException.class)
    public ResponseEntity<Map<String, String>> handleResourceAlreadyDisabledExceptions(ResourceAlreadyDisabledException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "This resource is already disabled");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
    }

    @ExceptionHandler(ResourceForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleResourceForbiddenExceptions(ResourceForbiddenException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleServiceUnavailableException(ServiceUnavailableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", err_msg);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errors);
    }
}
